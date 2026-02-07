package com.auth.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.auth.service.dto.ApiResponse;
import com.auth.service.constants.Constants;
import com.auth.service.exception.*;
import com.auth.service.dto.RegisterRequestDto;
import com.auth.service.model.*;
import com.auth.service.repository.RoleRepository;
import com.auth.service.repository.TokenRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final JWTUtils jwtUtils;

    /**
     * Register a new user
     */
    @Transactional
    public ApiResponse register(RegisterRequestDto request) throws UserAlreadyExistsException {
        log.info("Registering new user: {}",
                request.getEmail() != null ? request.getEmail() : request.getMobileNumber());

        // Validate required fields
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required for registration.");
        }

        // Validate mobile number
        String mobile = request.getMobileNumber();
        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required for registration.");
        }
        if (!mobile.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits.");
        }

        // Check if user already exists by email
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // Check if user already exists by mobile
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserAlreadyExistsException(
                    "User with mobile " + request.getMobileNumber() + " already exists.",
                    HttpStatus.BAD_REQUEST);
        }

        // Get role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + request.getRoleId()));

        // Build user entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mobileNumber(request.getMobileNumber())
                .role(role)
                .isUserVerified(
                        request.getIsUserVerified() != null ? request.getIsUserVerified() : UserVerified.PENDING)
                .status(Constants.ONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Set optional fields
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Generate tokens
        String jwtToken = jwtService.generateToken(user, request.getSource());
        jwtService.generateRefreshToken(user, request.getSource());
        saveUserToken(savedUser, jwtToken);

        return new ApiResponse("User registered successfully");
    }

    /**
     * Save user token
     */
    public void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Authenticate user with username/password
     */
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest,
            HttpServletRequest httpServletRequest) throws InvalidUserDetailsException, InvalidCredentialsException {

        String username = authenticationRequest.getUsername();
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Find user by email or mobile
        User user;
        if (username.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new InvalidUserDetailsException(Constants.INVALID_CREDENTIALS,
                            HttpStatus.BAD_REQUEST));
        } else {
            user = userRepository.findByMobileNumber(username)
                    .orElseThrow(() -> new InvalidUserDetailsException(Constants.INVALID_CREDENTIALS,
                            HttpStatus.BAD_REQUEST));
        }

        // Verify password
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, authenticationRequest.getPassword()));

        // Generate tokens
        String jwtToken = jwtService.generateToken(user, authenticationRequest.getSource());
        String refreshToken = jwtService.generateRefreshToken(user, authenticationRequest.getSource());

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        log.info("User {} authenticated successfully", user.getId());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("BEARER")
                .message("Login successful")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    /**
     * Revoke all tokens for a user
     */
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refresh JWT token
     */
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(Constants.BEARER)) {
            return;
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String source = jwtUtils.JWTDecoder(refreshToken);
                String accessToken = jwtService.generateToken(user, source);

                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType(Constants.BEARER.trim())
                        .message(Constants.SUCCESS)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build();

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}

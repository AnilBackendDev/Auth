package com.auth.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.auth.service.dto.ApiResponse;
import com.auth.service.constants.Constants;
import com.auth.service.constants.WhatsAppTemplates;
import com.auth.service.exception.*;
import com.auth.notification.NotificationService;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RoleRepository roleRepository;

    private final JWTUtils JWTUtils;

    private final NotificationService notificationService;

    public ApiResponse register(RegisterRequestDto request) throws UserAlreadyExistsException {

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required for registration.");
        }

        String mobile = request.getMobileNumber();

        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required for registration.");
        }

        if (!mobile.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits.");
        }

        // Check if user already exists
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.",
                        HttpStatus.NOT_FOUND);
            }
        }

        if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
            if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
                throw new UserAlreadyExistsException(
                        "User with mobile " + request.getMobileNumber() + " already exists.",
                        HttpStatus.NOT_FOUND);
            }
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("role not found"));

        User u = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (Objects.equals(u.getRole().getRoleName(), "ADMIN")) {
            if (!Objects.equals(role.getRoleName(), "STOCKIST") && (!Objects.equals(role.getRoleName(), "MARKETING"))) {
                throw new IllegalArgumentException("admin can invite only stockist and marketing");
            }

            // boolean stockistExists =
            // addressRepository.existsStockistByState(request.getState()) == 1;
            // if (stockistExists) {
            // throw new IllegalArgumentException("Stockist already exists for state: " +
            // request.getState());
            // }
        }

        if (Objects.equals(u.getRole().getRoleName(), "STOCKIST")) {
            if (!Objects.equals(role.getRoleName(), "DISTRIBUTOR")) {
                throw new IllegalArgumentException("stockist can invite only distributor");
            }

            // boolean distributorExists = addressRepository
            // .existsDistributorByStateAndCity(request.getState(),request.getCity()) == 1;
            // if (distributorExists) {
            // throw new IllegalArgumentException("distributor already exists for state : "
            // + request.getState() + " " + " " + "city : " + " " + request.getCity());
            // }
        }

        if (Objects.equals(u.getRole().getRoleName(), "DISTRIBUTOR")) {
            if (!Objects.equals(role.getRoleName(), "RETAILER")) {
                throw new IllegalArgumentException("distributor can invite only retailer");
            }
        }
        // if(request.getEmail() == null){
        // request.setEmail(request.getMobileNumber()+"@gmail.com");
        // }
        //
        // if(request.getPassword() == null){
        // request.setPassword(request.getFirstName());
        // }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                // .email(request.getEmail())
                // .password(passwordEncoder.encode(request.getFirstName()))
                .mobileNumber(request.getMobileNumber())
                .role(role)
                // .isUserVerified(request.getIsUserVerified())
                .isUserVerified(
                        request.getIsUserVerified() != null ? request.getIsUserVerified() : UserVerified.PENDING) // fallback
                .status(Constants.ONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(request.getId())
                .updatedBy(request.getId())
                .companyName(request.getCompanyName())
                .gst(request.getGst())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .isUpdated(0)
                .build();
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user, request.getSource());
        jwtService.generateRefreshToken(user, request.getSource());
        saveUserToken(savedUser, jwtToken);

        try {
            // Format mobile for sending
            String mobileNumber = savedUser.getMobileNumber();
            if (mobileNumber != null) {
                if (mobileNumber.startsWith("+91")) {
                    mobileNumber = mobileNumber.substring(1);
                } else if (!mobileNumber.startsWith("91")) {
                    mobileNumber = "91" + mobileNumber;
                }
            } else {
                mobileNumber = "";
            }

            String fullName = savedUser.getFirstName() != null ? savedUser.getFirstName() : "";
            String userRole = (savedUser.getRole() != null && savedUser.getRole().getRoleName() != null)
                    ? savedUser.getRole().getRoleName().toUpperCase()
                    : "";
            String inviterName = u.getFirstName() != null ? u.getFirstName() : "";
            String loginMobile = savedUser.getMobileNumber() != null
                    ? savedUser.getMobileNumber().replace("+91", "").replaceFirst("^91", "")
                    : "";

            log.info("WhatsApp params - fullName: '{}', inviterName: '{}', userRole: '{}', loginMobile: '{}'",
                    fullName, inviterName, userRole, loginMobile);

            // Correct order for your desired message:
            // {{1}} = fullName (Ojas)
            // {{2}} = inviterName (ooge)
            // {{3}} = userRole (STOCKIST)
            // {{4}} = loginMobile (7670902871)
            List<String> templateParams = List.of(
                    fullName, // {{1}}
                    inviterName, // {{2}}
                    userRole, // {{3}}
                    loginMobile // {{4}}
            );

            String templateName = WhatsAppTemplates.OOGE_ACCESS_REVIEW;

            notificationService.sendWhatsAppTextMessage(
                    mobileNumber,
                    templateName,
                    templateParams,
                    "",
                    false,
                    "");

        } catch (Exception e) {
            log.error("Failed to send WhatsApp message", e);
        }

        return new ApiResponse("The user has successfully registered.");
    }

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest,
            HttpServletRequest httpServletRequest)
            throws InvalidUserDetailsException, InvalidCredentialsException {

        // logger.info("Authenticating user: {}", authenticationRequest.getUsername());
        String user_name = authenticationRequest.getUsername();
        if (user_name == null) {
            // logger.error("Username cannot be null");
            throw new IllegalArgumentException("Username cannot be null");
        }

        User user = authenticationRequest.getUsername().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                ? userRepository.findByEmail(authenticationRequest.getUsername())
                        .orElseThrow(() -> new InvalidUserDetailsException(Constants.INVALID_CREDENTIALS,
                                HttpStatus.BAD_REQUEST))
                : userRepository.findByMobileNumber(authenticationRequest.getUsername())
                        .orElseThrow(() -> new InvalidUserDetailsException(Constants.INVALID_CREDENTIALS,
                                HttpStatus.BAD_REQUEST));

        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));
        var jwtToken = jwtService.generateToken(user, authenticationRequest.getSource());
        var refreshToken = jwtService.generateRefreshToken(user, authenticationRequest.getSource());
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("BEARER")
                .build();
    }

    public void revokeAllUserTokens(User user) {
        // logger.info("Revoking all tokens for user ID: {}", user.getId());
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(Constants.BEARER)) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                String source = JWTUtils.JWTDecoder(refreshToken);
                var accessToken = jwtService.generateToken(user, source);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType(Constants.BEARER.trim())
                        .message(Constants.SUCCESS)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}

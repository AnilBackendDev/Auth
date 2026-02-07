package com.auth.service.service;

import com.auth.service.dto.OAuth2LoginRequestDto;
import com.auth.service.exception.NotFoundException;
import com.auth.service.model.AuthenticationResponse;
import com.auth.service.model.Role;
import com.auth.service.model.User;
import com.auth.service.model.UserVerified;
import com.auth.service.repository.RoleRepository;
import com.auth.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @Transactional
    public AuthenticationResponse handleOAuth2Login(OAuth2LoginRequestDto request, String source) {
        log.info("Processing OAuth2 login for provider: {}, email: {}", request.getOauthProvider(), request.getEmail());

        // Check if user already exists with this OAuth provider
        Optional<User> existingUserOpt = userRepository.findByOauthProviderAndOauthProviderId(
                request.getOauthProvider(), request.getOauthProviderId());

        User user;
        boolean isNewUser = false;

        if (existingUserOpt.isPresent()) {
            // User exists with OAuth - just login
            user = existingUserOpt.get();
            log.info("Existing OAuth user found: {}", user.getId());

            // Update user info if changed
            boolean updated = false;
            if (!user.getEmail().equals(request.getEmail())) {
                user.setEmail(request.getEmail());
                updated = true;
            }
            if (request.getFirstName() != null && !request.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(request.getFirstName());
                updated = true;
            }
            if (request.getLastName() != null && !request.getLastName().equals(user.getLastName())) {
                user.setLastName(request.getLastName());
                updated = true;
            }

            if (updated) {
                user.setUpdatedAt(LocalDateTime.now());
                user = userRepository.save(user);
            }
        } else {
            // Check if user exists with same email but different provider
            Optional<User> emailUserOpt = userRepository.findByEmail(request.getEmail());

            if (emailUserOpt.isPresent()) {
                // User exists with same email - link OAuth to existing account
                user = emailUserOpt.get();
                user.setOauthProvider(request.getOauthProvider());
                user.setOauthProviderId(request.getOauthProviderId());
                user.setUpdatedAt(LocalDateTime.now());
                user = userRepository.save(user);
                log.info("Linked OAuth provider to existing user: {}", user.getId());
            } else {
                // Create new user
                user = createNewOAuthUser(request);
                isNewUser = true;
                log.info("Created new OAuth user: {}", user.getId());
            }
        }

        // Generate tokens
        var jwtToken = jwtService.generateToken(user, source);
        var refreshToken = jwtService.generateRefreshToken(user, source);

        // Revoke old tokens and save new one
        authenticationService.revokeAllUserTokens(user);
        authenticationService.saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("BEARER")
                .message(isNewUser ? "User registered successfully" : "Login successful")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private User createNewOAuthUser(OAuth2LoginRequestDto request) {
        // Determine role - default to a basic role or use provided roleId
        Role role;
        if (request.getRoleId() != null) {
            role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new NotFoundException("Role not found with id: " + request.getRoleId()));
        } else {
            // Default to a basic user role - adjust this based on your role structure
            // Assuming role ID 5 or find by name
            role = roleRepository.findByRoleName("USER")
                    .orElseGet(() -> {
                        // If USER role doesn't exist, try to get first non-admin role
                        return roleRepository.findById(2)
                                .orElseThrow(() -> new NotFoundException("Default role not found"));
                    });
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .oauthProvider(request.getOauthProvider())
                .oauthProviderId(request.getOauthProviderId())
                .role(role)
                .isUserVerified(UserVerified.VERIFIED) // OAuth users are pre-verified
                .status(1) // Active
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public AuthenticationResponse registerWithOAuth(OAuth2LoginRequestDto request, String source) {
        log.info("Registering new user with OAuth2: {}, email: {}", request.getOauthProvider(), request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User user = createNewOAuthUser(request);

        // Generate tokens
        var jwtToken = jwtService.generateToken(user, source);
        var refreshToken = jwtService.generateRefreshToken(user, source);
        authenticationService.saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("BEARER")
                .message("User registered successfully with " + request.getOauthProvider())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}

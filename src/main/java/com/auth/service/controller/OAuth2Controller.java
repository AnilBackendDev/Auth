package com.auth.service.controller;

import com.auth.service.dto.OAuth2LoginRequestDto;
import com.auth.service.model.AuthenticationResponse;
import com.auth.service.service.OAuth2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;

    /**
     * Endpoint for Google OAuth2 login/signup
     * This can be called from frontend after receiving Google OAuth token
     */
    @PostMapping("/google/login")
    public ResponseEntity<AuthenticationResponse> googleLogin(
            @RequestBody @Valid OAuth2LoginRequestDto request,
            @RequestParam(defaultValue = "web") String source) {
        try {
            log.info("Google OAuth login request for email: {}", request.getEmail());

            // Set provider to GOOGLE
            request.setOauthProvider("GOOGLE");

            // Handle login - will create user if doesn't exist
            AuthenticationResponse response = oauth2Service.handleOAuth2Login(request, source);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Google OAuth request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Google OAuth login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthenticationResponse.builder()
                            .message("An error occurred during Google login: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Explicit signup endpoint with OAuth
     * Requires roleId to be specified
     */
    @PostMapping("/google/signup")
    public ResponseEntity<AuthenticationResponse> googleSignup(
            @RequestBody @Valid OAuth2LoginRequestDto request,
            @RequestParam(defaultValue = "web") String source) {
        try {
            log.info("Google OAuth signup request for email: {}", request.getEmail());

            if (request.getRoleId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(AuthenticationResponse.builder()
                                .message("Role ID is required for signup")
                                .build());
            }

            // Set provider to GOOGLE
            request.setOauthProvider("GOOGLE");

            // Handle signup
            AuthenticationResponse response = oauth2Service.registerWithOAuth(request, source);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Google OAuth signup request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Google OAuth signup error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthenticationResponse.builder()
                            .message("An error occurred during Google signup: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Health check endpoint for OAuth2 configuration
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("OAuth2 service is running");
    }
}

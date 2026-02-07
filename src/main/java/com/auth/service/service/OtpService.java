package com.auth.service.service;

import com.auth.service.dto.*;
import com.auth.service.exception.InvalidCredentialsException;
import com.auth.service.exception.NotFoundException;
import com.auth.service.model.*;
import com.auth.service.repository.OtpRepository;
import com.auth.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    /**
     * Generate and send OTP to mobile number
     */
    @Transactional
    public ApiResponse sendOtp(OtpRequestDto request) {
        String mobileNumber = request.getMobileNumber();
        log.info("Sending OTP to mobile: {}", mobileNumber);

        // Generate 6-digit OTP
        String otpValue = generateOtp();

        // Save OTP to database
        Otp otp = Otp.builder()
                .mobileNumber(mobileNumber)
                .otpValue(otpValue)
                .expirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .isVerified(false)
                .build();

        log.debug("Saving OTP for mobile: {}, value: {}", mobileNumber, otpValue);
        Otp savedOtp = otpRepository.save(otp);
        log.info("OTP saved to database with ID: {}", savedOtp.getId());

        // TODO: Integrate with SMS service to send OTP
        // For now, log the OTP (remove in production!)
        log.info("OTP generated for {}: {} (remove this log in production)", mobileNumber, otpValue);

        return new ApiResponse("OTP sent successfully to " + maskMobileNumber(mobileNumber));
    }

    /**
     * Verify OTP
     */
    @Transactional
    public ApiResponse verifyOtp(OtpVerifyDto request) {
        String mobileNumber = request.getMobileNumber();
        String providedOtp = request.getOtp();

        Otp otp = otpRepository.findLatestByMobileNumber(mobileNumber)
                .orElseThrow(() -> new NotFoundException("No OTP found for this mobile number"));

        if (otp.isExpired()) {
            throw new InvalidCredentialsException("OTP has expired. Please request a new one.");
        }

        if (!otp.getOtpValue().equals(providedOtp)) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        // Mark OTP as verified
        otp.setIsVerified(true);
        otpRepository.save(otp);

        return new ApiResponse("OTP verified successfully");
    }

    /**
     * Login user with mobile number and OTP (no password needed)
     */
    @Transactional
    public AuthenticationResponse loginWithOtp(OtpVerifyDto request, String source) {
        String mobileNumber = request.getMobileNumber();
        String providedOtp = request.getOtp();

        // Verify OTP first
        Otp otp = otpRepository.findLatestByMobileNumber(mobileNumber)
                .orElseThrow(() -> new NotFoundException("No OTP found for this mobile number"));

        if (otp.isExpired()) {
            throw new InvalidCredentialsException("OTP has expired. Please request a new one.");
        }

        if (!otp.getOtpValue().equals(providedOtp)) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        // Find user by mobile number
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new NotFoundException("User not found with this mobile number"));

        // Mark OTP as verified
        otp.setIsVerified(true);
        otpRepository.save(otp);

        // Generate tokens
        String jwtToken = jwtService.generateToken(user, source);
        String refreshToken = jwtService.generateRefreshToken(user, source);

        authenticationService.revokeAllUserTokens(user);
        authenticationService.saveUserToken(user, jwtToken);

        log.info("User {} logged in successfully via OTP", user.getId());

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
     * Send OTP for forgot password
     */
    @Transactional
    public ApiResponse sendForgotPasswordOtp(OtpRequestDto request) {
        String mobileNumber = request.getMobileNumber();

        // Check if user exists
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new NotFoundException("User not found with this mobile number"));

        // Generate and save OTP
        String otpValue = generateOtp();
        Otp otp = Otp.builder()
                .mobileNumber(mobileNumber)
                .userId(user.getId())
                .otpValue(otpValue)
                .expirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .isVerified(false)
                .build();
        otpRepository.save(otp);

        // TODO: Send OTP via SMS
        log.info("Forgot password OTP for {}: {} (remove in production)", mobileNumber, otpValue);

        return new ApiResponse("OTP sent to your registered mobile number");
    }

    /**
     * Reset password with OTP verification
     */
    @Transactional
    public ApiResponse resetPassword(ForgotPasswordDto request) {
        String identifier = request.getIdentifier();
        String providedOtp = request.getOtp();
        String newPassword = request.getNewPassword();

        // Find user by mobile or email
        Optional<User> userOpt = userRepository.findByMobileNumber(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier);
        }
        User user = userOpt.orElseThrow(() -> new NotFoundException("User not found"));

        // Verify OTP
        Otp otp = otpRepository.findLatestByMobileNumber(user.getMobileNumber())
                .orElseGet(() -> otpRepository.findLatestByUserId(user.getId())
                        .orElseThrow(() -> new NotFoundException("No OTP found. Please request a new one.")));

        if (otp.isExpired()) {
            throw new InvalidCredentialsException("OTP has expired. Please request a new one.");
        }

        if (!otp.getOtpValue().equals(providedOtp)) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Mark OTP as used
        otp.setIsVerified(true);
        otpRepository.save(otp);

        // Revoke all existing tokens for security
        authenticationService.revokeAllUserTokens(user);

        log.info("Password reset successfully for user {}", user.getId());

        return new ApiResponse("Password reset successfully. Please login with your new password.");
    }

    /**
     * Update password (authenticated user)
     */
    @Transactional
    public ApiResponse updatePassword(UpdatePasswordDto request, User currentUser) {
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);

        // Revoke all existing tokens for security
        authenticationService.revokeAllUserTokens(currentUser);

        log.info("Password updated successfully for user {}", currentUser.getId());

        return new ApiResponse("Password updated successfully. Please login again.");
    }

    /**
     * Generate random OTP
     */
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Mask mobile number for display
     */
    private String maskMobileNumber(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return "****";
        }
        return "****" + mobile.substring(mobile.length() - 4);
    }
}

package com.auth.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "otp_value", nullable = false)
    private String otpValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expirationTime == null) {
            expirationTime = LocalDateTime.now().plusMinutes(5); // Default 5 min expiry
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
}

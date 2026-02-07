package com.auth.service.repository;

import com.auth.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * User Repository - Essential queries for authentication service
 */
public interface UserRepository extends JpaRepository<User, Integer> {

        // ==================== EXISTENCE CHECKS ====================

        boolean existsByEmail(String email);

        boolean existsByMobileNumber(String mobileNumber);

        // ==================== FIND BY EMAIL ====================

        @Query("SELECT u FROM User u WHERE u.email = :email AND u.isUserVerified = VERIFIED")
        Optional<User> findByEmail(@Param("email") String email);

        @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions WHERE u.email = :email")
        Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

        // ==================== FIND BY MOBILE ====================

        @Query(value = "SELECT * FROM user WHERE mobile_number = :mobileNumber AND is_user_verified = 'VERIFIED' ORDER BY id DESC LIMIT 1", nativeQuery = true)
        Optional<User> findByMobileNumber(@Param("mobileNumber") String mobileNumber);

        @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions WHERE u.mobileNumber = :mobileNumber")
        Optional<User> findByMobileWithRolesAndPermissions(@Param("mobileNumber") String mobileNumber);

        // ==================== FIND ANY MOBILE (for OTP login before verification)
        // ====================

        @Query(value = "SELECT * FROM user WHERE mobile_number = :mobileNumber ORDER BY id DESC LIMIT 1", nativeQuery = true)
        Optional<User> findAnyByMobileNumber(@Param("mobileNumber") String mobileNumber);

        // ==================== OAUTH2 ====================

        Optional<User> findByOauthProviderAndOauthProviderId(String oauthProvider, String oauthProviderId);

        Optional<User> findByEmailAndOauthProvider(String email, String oauthProvider);
}

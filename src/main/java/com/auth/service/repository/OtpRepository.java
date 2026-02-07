package com.auth.service.repository;

import com.auth.service.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Integer> {

    @Query("SELECT o FROM Otp o WHERE o.mobileNumber = :mobileNumber AND o.isVerified = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<Otp> findLatestByMobileNumber(@Param("mobileNumber") String mobileNumber);

    @Query("SELECT o FROM Otp o WHERE o.userId = :userId AND o.isVerified = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<Otp> findLatestByUserId(@Param("userId") Integer userId);

    void deleteByMobileNumber(String mobileNumber);

    void deleteByUserId(Integer userId);
}

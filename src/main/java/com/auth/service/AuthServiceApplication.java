package com.auth.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Authentication Service Application
 * 
 * Handles role-based authentication and authorization
 * 
 * Features:
 * - User Registration with role assignment
 * - User Login with JWT token generation
 * - Role and Permission management
 * - Password management (change, forgot, reset)
 * - OTP generation and validation
 * - Token management and validation
 * 
 * @version 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.auth.service", "com.auth.notification" })
@EntityScan(basePackages = { "com.auth.service.model" })
@EnableJpaRepositories(basePackages = { "com.auth.service.repository" })
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("====================================================");
        System.out.println("   Authentication Service Started");
        System.out.println("   Port: 8081");
        System.out.println("   Ready to handle authentication requests!");
        System.out.println("====================================================");
    }
}

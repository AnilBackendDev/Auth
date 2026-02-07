package com.auth.service.repository;

import com.auth.service.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Token Repository - For JWT token management
 */
public interface TokenRepository extends JpaRepository<Token, Integer> {

    /**
     * Find all valid (non-expired, non-revoked) tokens for a user
     */
    @Query("""
            SELECT t FROM Token t INNER JOIN User u
            ON t.user.id = u.id
            WHERE u.id = :id AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAllValidTokenByUser(Integer id);

    /**
     * Find token by token string (for validation)
     */
    Optional<Token> findByToken(String token);
}

package com.auth.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String message;
    private String accessToken;
    private String refreshToken;
    // private TokenType tokenType;
    private String tokenType;
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;

    public AuthenticationResponse(String message) {
        this.message = message;
    }

}

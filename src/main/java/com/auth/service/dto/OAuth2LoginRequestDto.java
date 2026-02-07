package com.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2LoginRequestDto {
    private String email;
    private String firstName;
    private String lastName;
    private String oauthProvider;
    private String oauthProviderId;
    private String profilePicture;
    private Integer roleId; // For signup, user can choose their role
}

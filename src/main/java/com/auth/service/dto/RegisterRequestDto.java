package com.auth.service.dto;

import com.auth.service.model.UserVerified;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class RegisterRequestDto {

    // Basic required fields
    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    @NotNull(message = "Role ID is required")
    @Positive(message = "Role ID must be a positive number")
    private Integer roleId;

    private String password;

    // Status fields
    private UserVerified isUserVerified;
    private String source; // web, mobile, etc.

    // Optional business fields (can be added later by admin)
    private String companyName;
    private String gst;
    private String address;
    private String city;
    private String state;
}

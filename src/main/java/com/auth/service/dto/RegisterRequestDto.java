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

    private Integer id;
    @NotBlank(message = "first name should not be empty or null")
    private String firstName;
//    @NotBlank(message = "last name should not be empty or null")
    private String lastName;
//    @NotBlank(message = "email should not be empty or null")
    @Email(message = "email should be valid")
    private String email;
    @NotBlank(message = "mobile number should not be empty or null")
    private String mobileNumber;
    @NotNull(message = "role id should not be empty or null")
    @Positive(message = "role id must be a positive number")
    private Integer roleId;
//    @NotBlank(message = "password should not be empty or null")
    private String password;
    private UserVerified isUserVerified;
    private Integer status;
    private String dob;
    private String source;
    private String state;
    private String city;
    private String companyName;
    private String gst;
    private String address;
    private String reason;


}

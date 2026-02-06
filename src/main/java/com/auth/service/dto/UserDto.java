package com.auth.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.auth.service.model.UserVerified;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private int status;
    private UserVerified isUserVerified;
    private String state;
    private String roleName;
    private String companyName;
    private String gst;
    private String address;
    private String city;
    private String reason;
    private Integer updatedBy;
    private String alternativeMobileNumber;

    public UserDto(int id, String firstName, String lastName, String email,
            String mobileNumber, int status, UserVerified isUserVerified) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.isUserVerified = isUserVerified;
    }

    public UserDto(int id, String firstName, String lastName, String email,
            String mobileNumber, int status, UserVerified isUserVerified, String roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.isUserVerified = isUserVerified;
        this.roleName = roleName;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "dd MMM yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    public UserDto(int id, String firstName, String lastName, String email,
            String mobileNumber, int status, UserVerified isUserVerified,
            String roleName, String reason) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.isUserVerified = isUserVerified;
        this.roleName = roleName;
        this.reason = reason;
    }

    public UserDto(int id, String firstName, String lastName, String email,
            String mobileNumber, int status, UserVerified isUserVerified,
            String companyName, String city, String reason, String state, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.isUserVerified = isUserVerified;
        this.companyName = companyName;
        this.reason = reason;
        this.address = address;
        this.city = city;
        this.state = state;
    }

    public UserDto(int id, String firstName, String lastName, String email,
            String mobileNumber, int status, UserVerified isUserVerified,
            String roleName, String companyName, String gst,
            String city, String state, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.isUserVerified = isUserVerified;
        this.roleName = roleName;
        this.companyName = companyName;
        this.gst = gst;
        this.city = city;
        this.state = state;
        this.address = address;
    }

    public UserDto(int id, String firstName, String lastName, String email,
            String mobileNumber, int status, UserVerified isUserVerified,
            String roleName, String companyName, String gst,
            String city, String state, String address, String reason) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.isUserVerified = isUserVerified;
        this.roleName = roleName;
        this.companyName = companyName;
        this.gst = gst;
        this.city = city;
        this.state = state;
        this.address = address;
        this.reason = reason;
    }

}
package com.auth.service.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAlreadyExistsException extends Exception {
    private final HttpStatus statusCode;

    public UserAlreadyExistsException(String errorMessage, HttpStatus statusCode) {
        super(errorMessage);
        this.statusCode = statusCode;
    }

}

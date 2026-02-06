package com.auth.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidUserDetailsException extends Exception {
    private final HttpStatus statusCode;

    public InvalidUserDetailsException(String errorMessage, HttpStatus statusCode) {
        super(errorMessage);
        this.statusCode = statusCode;
    }
}

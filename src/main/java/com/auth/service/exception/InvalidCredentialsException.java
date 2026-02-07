package com.auth.service.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends RuntimeException {

    private String message;
    private HttpStatus statusCode;

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}

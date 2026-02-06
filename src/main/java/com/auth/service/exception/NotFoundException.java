package com.auth.service.exception;

public class NotFoundException extends RuntimeException {

    public String message;

    public NotFoundException(String message) {
        super(message);
    }
}
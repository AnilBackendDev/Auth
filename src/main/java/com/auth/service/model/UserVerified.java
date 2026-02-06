package com.auth.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserVerified {

    PENDING,VERIFIED,REJECTED,INACTIVE;

    @JsonCreator
    public static UserVerified fromString(String value) {
        for (UserVerified type : UserVerified.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid user verified : Accepted are either PENDING,VERIFIED,REJECTED ");
    }
}

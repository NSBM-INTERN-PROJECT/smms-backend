package com.smms.user.exception;

public class ProfileUpdateRequestNotFoundException extends RuntimeException {

    public ProfileUpdateRequestNotFoundException(String message) {
        super(message);
    }
}
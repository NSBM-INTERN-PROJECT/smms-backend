package com.smms.user.exception;

import org.springframework.http.HttpStatus;

public class ProfileNotFoundException extends UserException {
    public ProfileNotFoundException(Long userId) {
        super("PROFILE_NOT_FOUND", "Profile not found for userId: " + userId, HttpStatus.NOT_FOUND);
    }
}

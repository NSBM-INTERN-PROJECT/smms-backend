package com.smms.user.exception;

import org.springframework.http.HttpStatus;

public class DuplicateProfileException extends UserException {
    public DuplicateProfileException(String message) {
        super("DUPLICATE_PROFILE", message, HttpStatus.CONFLICT);
    }
}

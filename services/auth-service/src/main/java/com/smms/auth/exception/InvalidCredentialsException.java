package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}

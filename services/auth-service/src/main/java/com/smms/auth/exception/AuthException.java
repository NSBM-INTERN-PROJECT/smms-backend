package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all auth-service domain errors.
 * Subclasses provide specific error codes and HTTP status mappings.
 */
public class AuthException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public AuthException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

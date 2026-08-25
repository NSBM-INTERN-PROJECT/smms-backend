package com.smms.session.exception;

import org.springframework.http.HttpStatus;

public class SessionException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;
    public SessionException(String errorCode, String message, HttpStatus httpStatus) {
        super(message); this.errorCode = errorCode; this.httpStatus = httpStatus;
    }
    public String getErrorCode() { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}

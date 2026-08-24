package com.smms.allocation.exception;

import org.springframework.http.HttpStatus;

public class AllocationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public AllocationException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    public String getErrorCode() { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}

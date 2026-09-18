package com.smms.user.exception;

import org.springframework.http.HttpStatus;

public class RequestNotFoundException extends UserException {
    public RequestNotFoundException(Long id) {
        super("REQUEST_NOT_FOUND", "Request not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

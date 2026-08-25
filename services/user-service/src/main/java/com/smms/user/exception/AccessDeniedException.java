package com.smms.user.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends UserException {
    public AccessDeniedException() {
        super("ACCESS_DENIED", "You do not have permission to perform this action", HttpStatus.FORBIDDEN);
    }
}

package com.smms.session.exception;
import org.springframework.http.HttpStatus;
public class AccessDeniedException extends SessionException {
    public AccessDeniedException() {
        super("ACCESS_DENIED", "You do not have permission to perform this action", HttpStatus.FORBIDDEN);
    }
}

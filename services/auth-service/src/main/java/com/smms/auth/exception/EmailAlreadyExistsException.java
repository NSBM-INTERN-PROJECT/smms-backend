package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AuthException {

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS",
              "An account with email '" + email + "' already exists.",
              HttpStatus.CONFLICT);
    }
}

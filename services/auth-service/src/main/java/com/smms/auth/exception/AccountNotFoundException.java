package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends AuthException {

    public AccountNotFoundException(Long id) {
        super("ACCOUNT_NOT_FOUND", "Account not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public AccountNotFoundException(String email) {
        super("ACCOUNT_NOT_FOUND", "Account not found with email: " + email, HttpStatus.NOT_FOUND);
    }
}

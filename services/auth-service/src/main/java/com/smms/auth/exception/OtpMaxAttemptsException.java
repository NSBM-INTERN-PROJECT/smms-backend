package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

public class OtpMaxAttemptsException extends AuthException {
    public OtpMaxAttemptsException() {
        super("OTP_MAX_ATTEMPTS",
              "Maximum OTP verification attempts exceeded. Please request a new OTP.",
              HttpStatus.TOO_MANY_REQUESTS);
    }
}

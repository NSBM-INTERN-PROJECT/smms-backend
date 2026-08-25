package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

public class OtpExpiredException extends AuthException {
    public OtpExpiredException() {
        super("OTP_EXPIRED", "OTP has expired. Please request a new one.", HttpStatus.UNAUTHORIZED);
    }
}

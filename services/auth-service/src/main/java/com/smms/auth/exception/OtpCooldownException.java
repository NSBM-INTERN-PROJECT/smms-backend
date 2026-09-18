package com.smms.auth.exception;

import org.springframework.http.HttpStatus;

public class OtpCooldownException extends AuthException {

    public OtpCooldownException(long remainingSeconds) {
        super("OTP_COOLDOWN",
              "Please wait " + remainingSeconds + " seconds before requesting a new OTP.",
              HttpStatus.TOO_MANY_REQUESTS);
    }
}

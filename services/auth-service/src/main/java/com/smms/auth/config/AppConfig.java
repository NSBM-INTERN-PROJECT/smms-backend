package com.smms.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * Centralizes all externalized configuration values for use across auth-service.
 * Values are injected from config-server (auth-service.yml).
 */
@Configuration
@Getter
public class AppConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiry-ms:900000}")
    private long jwtAccessExpiryMs;

    @Value("${jwt.refresh-expiry-ms:604800000}")
    private long jwtRefreshExpiryMs;

    @Value("${otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${otp.max-attempts:3}")
    private int otpMaxAttempts;

    @Value("${otp.resend-cooldown-seconds:60}")
    private int otpResendCooldownSeconds;
}

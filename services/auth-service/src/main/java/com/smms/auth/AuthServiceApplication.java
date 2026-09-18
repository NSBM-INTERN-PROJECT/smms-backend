package com.smms.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SMMS Auth Service — Owned by Member 2
 * <p>
 * Responsibilities:
 * - Two-step OTP login (POST /api/auth/login → POST /api/auth/verify-otp)
 * - JWT access token (15 min) + refresh token (7 days) issuance
 * - OTP generation (SecureRandom, 6-digit), storage, and email dispatch
 * - Admin user account management
 * - Audit log recording for all auth events
 * - Scheduled OTP purge job (@Scheduled every 10 minutes)
 * <p>
 * Database: smms_auth
 * Port: 8081 (from config-server)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

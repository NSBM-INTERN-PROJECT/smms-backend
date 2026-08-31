package com.smms.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SMMS User Service
 * <p>
 * Responsibilities:
 * - Mentor profile management (Admin creates/updates via PUT /api/users/admin/profiles/mentor/{userId})
 * - Student profile management (Admin creates/updates via PUT /api/users/admin/profiles/student/{userId})
 * - Student extended profile form submission + mentor approval workflow (FR-003)
 * - Data collection requests from mentor to student groups (FR-006)
 * - My Profile endpoint for all roles (GET /api/users/profile/me)
 * <p>
 * Database: smms_users
 * Port: 8082 (from config-server)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

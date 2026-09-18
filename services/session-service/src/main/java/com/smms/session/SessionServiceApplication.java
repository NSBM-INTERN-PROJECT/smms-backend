package com.smms.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SMMS Session Service
 * <p>
 * Responsibilities:
 * - Session note creation and management (FR-012)
 * - Progress status tracking (ON_TRACK, NEEDS_ATTENTION, AT_RISK, CRITICAL)
 * - Escalation creation and lifecycle management (FR-013)
 * - Automatic coordinator notification on AT_RISK/CRITICAL status
 * <p>
 * Database: smms_session
 * Port: 8085 (from config-server)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SessionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SessionServiceApplication.class, args);
    }
}

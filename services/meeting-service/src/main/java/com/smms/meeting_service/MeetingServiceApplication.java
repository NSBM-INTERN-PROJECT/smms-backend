package com.smms.meeting_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SMMS Meeting Service 
 * <p>
 * Responsibilities:
 * - Bulk meeting slot creation with group student notification (FR-007)
 * - Daily schedule and attendance tracking (FR-008)
 * - Meeting lifecycle management: reschedule, cancel, complete (FR-009)
 * - Student-initiated meeting requests (FR-010)
 * - In-app notifications (all types) + email reminders
 * <p>
 * Database: smms_meeting
 * Port: 8084 (from config-server)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class MeetingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeetingServiceApplication.class, args);
    }
}

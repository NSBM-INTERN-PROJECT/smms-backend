package com.smms.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SMMS Report Service 
 * <p>
 * Responsibilities:
 * - Role-specific dashboard metrics (FR-014)
 * - Filterable data exports: CSV, Excel (.xlsx), PDF (FR-015)
 * - Advanced mentor student filtering view (FR-005)
 * <p>
 * NO DATABASE — this service aggregates data by calling:
 * - user-service (profiles)
 * - allocation-service (allocations)
 * - meeting-service (meetings, attendance)
 * - session-service (notes, escalations, progress status)
 * <p>
 * Port: 8086 (from config-server)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ReportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}

package com.smms.allocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SMMS Allocation Service 
 * <p>
 * Responsibilities:
 * - Manual mentor-student allocation (Coordinator)
 * - Random auto-allocation engine using greedy-fill algorithm (FR-004)
 * - Transfer allocations between mentors
 * - Track allocation history and types (MANUAL vs RANDOM)
 * <p>
 * Database: smms_alloc
 * Port: 8083 (from config-server)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AllocationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AllocationServiceApplication.class, args);
    }
}

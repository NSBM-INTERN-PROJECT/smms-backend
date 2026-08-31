package com.smms.allocation.client;

import com.smms.allocation.client.dto.MentorCapacityDto;
import com.smms.allocation.client.dto.StudentSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for user-service.
 * Used by the random allocation engine to:
 * 1. Fetch all active students (with optional batch/dept filter)
 * 2. Fetch all active mentors with their maxStudents capacity
 *
 * These calls go through the API Gateway (service discovery via Eureka).
 */
@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {

    /**
     * Fetches a flat list of all active student user IDs, optionally filtered
     * by batch and/or department. The user-service exposes this via its admin endpoint.
     */
    @GetMapping("/internal/students/ids")
    List<StudentSummaryDto> getActiveStudents(
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String department);

    /**
     * Fetches a flat list of all active mentor user IDs with their maxStudents capacity.
     */
    @GetMapping("/internal/mentors/capacity")
    List<MentorCapacityDto> getActiveMentorsWithCapacity();
}

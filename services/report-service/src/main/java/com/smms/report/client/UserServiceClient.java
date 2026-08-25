package com.smms.report.client;

import com.smms.report.client.dto.MentorSummaryDto;
import com.smms.report.client.dto.StudentSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    /** Internal: all active students (optionally filtered). */
    @GetMapping("/api/users/internal/students/ids")
    List<StudentSummaryDto> getActiveStudents(
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String department);

    /** Internal: all active mentors with capacity. */
    @GetMapping("/api/users/internal/mentors/capacity")
    List<MentorSummaryDto> getActiveMentors();
}

package com.smms.report.client;

import com.smms.report.client.dto.MeetingDto;
import com.smms.report.client.dto.PagedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "meeting-service")
public interface MeetingServiceClient {

    @GetMapping("/api/meetings/mentor/me/history")
    PagedResponseDto<MeetingDto> getMentorMeetingHistory(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size);

    @GetMapping("/api/meetings/student/me/history")
    PagedResponseDto<MeetingDto> getStudentMeetingHistory(
            @RequestHeader("X-User-Id") Long studentUserId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size);
}

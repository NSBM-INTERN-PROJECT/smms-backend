package com.smms.report.client;

import com.smms.report.client.dto.EscalationDto;
import com.smms.report.client.dto.PagedResponseDto;
import com.smms.report.client.dto.SessionNoteDto;
import com.smms.report.client.dto.StudentProgressSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "session-service")
public interface SessionServiceClient {

    @GetMapping("/api/sessions/notes/student/{studentUserId}")
    PagedResponseDto<SessionNoteDto> getStudentNotes(
            @PathVariable Long studentUserId,
            @RequestHeader("X-User-Id") Long viewerUserId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size);

    @GetMapping("/api/sessions/notes/my-students/summary")
    List<StudentProgressSummaryDto> getMentorProgressSummary(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestHeader("X-User-Role") String role);

    @GetMapping("/api/sessions/escalations")
    PagedResponseDto<EscalationDto> listAllEscalations(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category);

    @GetMapping("/api/sessions/escalations/student/{studentUserId}")
    PagedResponseDto<EscalationDto> getStudentEscalations(
            @PathVariable Long studentUserId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size);
}

package com.smms.session.controller;

import com.smms.session.domain.EscalationCategory;
import com.smms.session.domain.EscalationStatus;
import com.smms.session.dto.request.*;
import com.smms.session.dto.response.*;
import com.smms.session.service.EscalationService;
import com.smms.session.service.SessionNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Service", description = "Session notes, progress tracking, and escalations")
public class SessionController {

    private final SessionNoteService noteService;
    private final EscalationService escalationService;

    // ─── Session Notes ──────────────────────────────────────────────────────────

    @Operation(summary = "Create a session note after a meeting (Mentor)")
    @PostMapping("/notes")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SessionNoteResponse> createNote(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody CreateSessionNoteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.create(mentorUserId, req));
    }

    @Operation(summary = "Get session note for a specific meeting")
    @GetMapping("/notes/meeting/{meetingId}")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN','COORDINATOR','STUDENT')")
    public ResponseEntity<SessionNoteResponse> getNoteByMeeting(
            @PathVariable Long meetingId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(noteService.getByMeetingId(meetingId, userId, role));
    }

    @Operation(summary = "Get session note history for a student (private notes redacted for students)")
    @GetMapping("/notes/student/{studentUserId}")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN','COORDINATOR','STUDENT')")
    public ResponseEntity<PagedResponse<SessionNoteResponse>> getStudentHistory(
            @PathVariable Long studentUserId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(noteService.getStudentHistory(studentUserId, userId, role, page, size));
    }

    @Operation(summary = "Update a session note (Mentor — own notes only)")
    @PutMapping("/notes/{id}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SessionNoteResponse> updateNote(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestBody UpdateSessionNoteRequest req) {
        return ResponseEntity.ok(noteService.update(id, mentorUserId, req));
    }

    @Operation(summary = "Get progress summary for all of my students (Mentor)")
    @GetMapping("/notes/my-students/summary")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<StudentProgressSummary>> getProgressSummary(
            @RequestHeader("X-User-Id") Long mentorUserId) {
        return ResponseEntity.ok(noteService.getMentorProgressSummary(mentorUserId));
    }

    // ─── Escalations ───────────────────────────────────────────────────────────

    @Operation(summary = "Raise an escalation for a student (Mentor)")
    @PostMapping("/escalations")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<EscalationResponse> createEscalation(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody CreateEscalationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(escalationService.create(mentorUserId, req));
    }

    @Operation(summary = "List all escalations with optional filters (Admin/Coordinator)")
    @GetMapping("/escalations")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<PagedResponse<EscalationResponse>> listEscalations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) EscalationStatus status,
            @RequestParam(required = false) EscalationCategory category) {
        return ResponseEntity.ok(escalationService.listAll(page, size, status, category));
    }

    @Operation(summary = "Get escalations for a specific student")
    @GetMapping("/escalations/student/{studentUserId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','MENTOR')")
    public ResponseEntity<PagedResponse<EscalationResponse>> getStudentEscalations(
            @PathVariable Long studentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(escalationService.getForStudent(studentUserId, page, size));
    }

    @Operation(summary = "Get escalations raised by me (Mentor)")
    @GetMapping("/escalations/my")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<PagedResponse<EscalationResponse>> getMyEscalations(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(escalationService.getForMentor(mentorUserId, page, size));
    }

    @Operation(summary = "Update escalation status (Admin/Coordinator)")
    @PutMapping("/escalations/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<EscalationResponse> updateEscalationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEscalationStatusRequest req) {
        return ResponseEntity.ok(escalationService.updateStatus(id, req));
    }
}

package com.smms.meeting_service.controller;

import com.smms.meeting_service.dto.request.*;
import com.smms.meeting_service.dto.response.*;
import com.smms.meeting_service.service.*;
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
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Tag(name = "Meeting Service", description = "Slots, meetings, attendance, and notifications")
public class MeetingController {

    private final SlotService slotService;
    private final MeetingService meetingService;
    private final MeetingRequestService requestService;
    private final NotificationService notificationService;

    // ─── Slots ───────────────────────────────────────────────────────────────────

    @Operation(summary = "Bulk create meeting slots (Mentor). Optionally auto-assigns students.")
    @PostMapping("/slots")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<BulkSlotResult> bulkCreateSlots(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody BulkCreateSlotRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.bulkCreate(mentorUserId, req));
    }

    @Operation(summary = "Get my slots (Mentor)")
    @GetMapping("/slots/mentor/me")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<PagedResponse<SlotResponse>> getMySlots(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(slotService.getMentorSlots(mentorUserId, page, size));
    }

    @Operation(summary = "Cancel a slot (Mentor)")
    @PutMapping("/slots/{slotId}/cancel")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SlotResponse> cancelSlot(
            @PathVariable Long slotId,
            @RequestHeader("X-User-Id") Long mentorUserId) {
        return ResponseEntity.ok(slotService.cancelSlot(slotId, mentorUserId));
    }

    @Operation(summary = "Student responds to a slot allocation (ACCEPTED / RESCHEDULE_REQUESTED)")
    @PutMapping("/slot-allocations/{id}/respond")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SlotAllocationResponse> respondToSlot(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long studentUserId,
            @Valid @RequestBody SlotResponseRequest req) {
        return ResponseEntity.ok(slotService.respondToSlot(id, studentUserId, req));
    }

    // ─── Meetings ────────────────────────────────────────────────────────────────

    @Operation(summary = "Get today's meetings (Mentor)")
    @GetMapping("/mentor/me/today")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<MeetingResponse>> getTodaysMeetings(
            @RequestHeader("X-User-Id") Long mentorUserId) {
        return ResponseEntity.ok(meetingService.getTodaysMeetings(mentorUserId));
    }

    @Operation(summary = "Get upcoming meetings (Mentor)")
    @GetMapping("/mentor/me/upcoming")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<MeetingResponse>> getUpcomingMeetings(
            @RequestHeader("X-User-Id") Long mentorUserId) {
        return ResponseEntity.ok(meetingService.getUpcomingForMentor(mentorUserId));
    }

    @Operation(summary = "Get my meeting history (Mentor)")
    @GetMapping("/mentor/me/history")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<PagedResponse<MeetingResponse>> getMentorHistory(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(meetingService.getMentorHistory(mentorUserId, page, size));
    }

    @Operation(summary = "Get my upcoming meetings (Student)")
    @GetMapping("/student/me/upcoming")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<MeetingResponse>> getStudentUpcoming(
            @RequestHeader("X-User-Id") Long studentUserId) {
        return ResponseEntity.ok(meetingService.getUpcomingForStudent(studentUserId));
    }

    @Operation(summary = "Get my meeting history (Student)")
    @GetMapping("/student/me/history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PagedResponse<MeetingResponse>> getStudentHistory(
            @RequestHeader("X-User-Id") Long studentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(meetingService.getStudentHistory(studentUserId, page, size));
    }

    @Operation(summary = "Mark attendance for a meeting (Mentor)")
    @PutMapping("/{id}/attendance")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MeetingResponse> markAttendance(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody AttendanceRequest req) {
        return ResponseEntity.ok(meetingService.markAttendance(id, mentorUserId, req));
    }

    @Operation(summary = "Reschedule a meeting (Mentor)")
    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MeetingResponse> reschedule(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody RescheduleMeetingRequest req) {
        return ResponseEntity.ok(meetingService.reschedule(id, mentorUserId, req));
    }

    @Operation(summary = "Cancel a meeting (Mentor)")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MeetingResponse> cancel(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestBody(required = false) CancelRequest req) {
        return ResponseEntity.ok(meetingService.cancel(id, mentorUserId,
                req != null ? req : new CancelRequest()));
    }

    @Operation(summary = "Mark a meeting complete (Mentor)")
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MeetingResponse> complete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId) {
        return ResponseEntity.ok(meetingService.complete(id, mentorUserId));
    }

    // ─── Meeting Requests ─────────────────────────────────────────────────────────

    @Operation(summary = "Student submits a meeting request to their mentor")
    @PostMapping("/requests")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MeetingRequestResponse> submitRequest(
            @RequestHeader("X-User-Id") Long studentUserId,
            @Valid @RequestBody MeetingRequestDto req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.submit(studentUserId, req));
    }

    @Operation(summary = "Mentor views pending meeting requests")
    @GetMapping("/requests/mentor/me/pending")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<PagedResponse<MeetingRequestResponse>> getPendingRequests(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(requestService.getPendingForMentor(mentorUserId, page, size));
    }

    @Operation(summary = "Student views their own request history")
    @GetMapping("/requests/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PagedResponse<MeetingRequestResponse>> getMyRequests(
            @RequestHeader("X-User-Id") Long studentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(requestService.getStudentHistory(studentUserId, page, size));
    }

    @Operation(summary = "Mentor approves a meeting request (creates scheduled meeting)")
    @PutMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MeetingResponse> approveRequest(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestHeader("X-Allocation-Id") Long allocationId,
            @Valid @RequestBody ReviewMeetingRequest review) {
        return ResponseEntity.ok(requestService.approve(id, mentorUserId, allocationId, review));
    }

    @Operation(summary = "Mentor rejects a meeting request")
    @PutMapping("/requests/{id}/reject")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MeetingRequestResponse> rejectRequest(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody ReviewMeetingRequest review) {
        return ResponseEntity.ok(requestService.reject(id, mentorUserId, review));
    }

    // ─── Notifications ──────────────────────────────────────────────────────────

    @Operation(summary = "Get my notifications")
    @GetMapping("/notifications")
    public ResponseEntity<PagedResponse<NotificationResponse>> getNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId, page, size));
    }

    @Operation(summary = "Get unread notification count")
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "Mark all notifications as read")
    @PutMapping("/notifications/mark-all-read")
    public ResponseEntity<Void> markAllRead(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.noContent().build();
    }
}

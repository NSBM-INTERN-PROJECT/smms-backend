package com.smms.user.controller;

import com.smms.user.dto.request.*;
import com.smms.user.dto.response.*;
import com.smms.user.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Single controller for all user-service endpoints.
 * Role enforcement via @PreAuthorize (reads SecurityContext set by RoleHeaderAuthFilter).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Service", description = "Profile and data collection management")
public class UserController {

    private final MentorProfileService mentorService;
    private final StudentProfileService studentService;
    private final ExtendedProfileService extendedService;
    private final ProfileUpdateService profileUpdateService;
    private final DataCollectionService dataCollectionService;

    // ─── My Profile ──────────────────────────────────────────────────────────

    @Operation(summary = "Get the calling user's own profile")
    @GetMapping("/profile/me")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        return switch (role.toUpperCase()) {
            case "MENTOR", "COORDINATOR" -> ResponseEntity.ok(mentorService.getByUserId(userId));
            case "STUDENT" -> ResponseEntity.ok(studentService.getByUserId(userId));
            default -> ResponseEntity.ok(java.util.Map.of("userId", userId, "role", role));
        };
    }

    // ─── Admin: Mentor Profile Management ────────────────────────────────────

    @Operation(summary = "Create mentor profile (Admin only)")
    @PostMapping("/admin/profiles/mentor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorProfileResponse> createMentor(
            @Valid @RequestBody CreateMentorProfileRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mentorService.create(req));
    }

    @Operation(summary = "List all mentor profiles")
    @GetMapping("/admin/profiles/mentor")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<PagedResponse<MentorProfileResponse>> listMentors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(mentorService.listAll(page, size, department));
    }

    @Operation(summary = "Update mentor profile (Admin only)")
    @PutMapping("/admin/profiles/mentor/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorProfileResponse> updateMentor(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMentorProfileRequest req) {
        return ResponseEntity.ok(mentorService.update(userId, req));
    }

    // ─── Admin: Student Profile Management ───────────────────────────────────

    @Operation(summary = "Create student profile (Admin only)")
    @PostMapping("/admin/profiles/student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentProfileResponse> createStudent(
            @Valid @RequestBody CreateStudentProfileRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(req));
    }

    @Operation(summary = "List all student profiles (Admin/Coordinator/Mentor)")
    @GetMapping("/admin/profiles/student")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','MENTOR')")
    public ResponseEntity<PagedResponse<StudentProfileResponse>> listStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String riskStatus) {
        return ResponseEntity.ok(studentService.listAll(page, size, batch, department, riskStatus));
    }

    @Operation(summary = "Update student profile (Admin only)")
    @PutMapping("/admin/profiles/student/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR')")
    public ResponseEntity<StudentProfileResponse> updateStudent(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateStudentProfileRequest req) {
        return ResponseEntity.ok(studentService.update(userId, req));
    }

    // ─── Student: Extended Profile ────────────────────────────────────────────

    @Operation(summary = "Get my extended profile (Student)")
    @GetMapping("/students/me/extended-profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ExtendedProfileResponse> getExtended(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(extendedService.get(userId));
    }

    @Operation(summary = "Submit / update my extended profile (Student)")
    @PostMapping("/students/me/extended-profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ExtendedProfileResponse> submitExtended(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ExtendedProfileRequest req) {
        return ResponseEntity.ok(extendedService.submit(userId, req));
    }

    // ─── Profile Update Request Flow ──────────────────────────────────────────

    @Operation(summary = "Student requests a field change (needs mentor approval)")
    @PostMapping("/profile-update-requests")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProfileUpdateRequestResponse> requestFieldUpdate(
            @RequestHeader("X-User-Id") Long studentUserId,
            @RequestHeader("X-Mentor-Id") Long mentorUserId,
            @Valid @RequestBody ProfileUpdateFieldRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileUpdateService.submitRequest(studentUserId, mentorUserId, req));
    }

    @Operation(summary = "Mentor views pending update requests from their students")
    @GetMapping("/profile-update-requests/pending")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<PagedResponse<ProfileUpdateRequestResponse>> getPendingRequests(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(profileUpdateService.getPendingForMentor(mentorUserId, page, size));
    }

    @Operation(summary = "Student views their own request history")
    @GetMapping("/profile-update-requests/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PagedResponse<ProfileUpdateRequestResponse>> getMyRequests(
            @RequestHeader("X-User-Id") Long studentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(profileUpdateService.getStudentHistory(studentUserId, page, size));
    }

    @Operation(summary = "Mentor approves a profile update request")
    @PutMapping("/profile-update-requests/{id}/approve")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ProfileUpdateRequestResponse> approveRequest(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody ReviewUpdateRequest review) {
        return ResponseEntity.ok(profileUpdateService.approve(id, mentorUserId, review));
    }

    @Operation(summary = "Mentor rejects a profile update request")
    @PutMapping("/profile-update-requests/{id}/reject")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ProfileUpdateRequestResponse> rejectRequest(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody ReviewUpdateRequest review) {
        return ResponseEntity.ok(profileUpdateService.reject(id, mentorUserId, review));
    }

    // ─── Data Collection Requests ─────────────────────────────────────────────

    @Operation(summary = "Mentor creates a data-collection request for a group of students")
    @PostMapping("/data-collection-requests")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<DataCollectionResponse> createDataRequest(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @Valid @RequestBody DataCollectionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataCollectionService.create(mentorUserId, req));
    }

    @Operation(summary = "Get data-collection request details")
    @GetMapping("/data-collection-requests/{id}")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<DataCollectionResponse> getDataRequest(@PathVariable Long id) {
        return ResponseEntity.ok(dataCollectionService.getById(id));
    }

    @Operation(summary = "Student marks a data-collection task as submitted")
    @PostMapping("/data-collection-requests/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> submitDataCollection(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long studentUserId) {
        dataCollectionService.markSubmitted(id, studentUserId);
        return ResponseEntity.noContent().build();
    }
}

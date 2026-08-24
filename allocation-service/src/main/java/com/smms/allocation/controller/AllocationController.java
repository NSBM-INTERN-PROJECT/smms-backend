package com.smms.allocation.controller;

import com.smms.allocation.domain.AllocationStatus;
import com.smms.allocation.dto.request.*;
import com.smms.allocation.dto.response.*;
import com.smms.allocation.service.AllocationService;
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
@RequestMapping("/api/v1/allocations")
@RequiredArgsConstructor
@Tag(name = "Allocation Service", description = "Mentor-student allocation management")
public class AllocationController {

    private final AllocationService allocationService;

    @Operation(summary = "Manually allocate a student to a mentor (Admin/Coordinator)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<AllocationResponse> manualAllocate(
            @Valid @RequestBody ManualAllocationRequest req,
            @RequestHeader("X-User-Id") Long coordinatorUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(allocationService.manualAllocate(req, coordinatorUserId));
    }

    @Operation(summary = "Run random allocation engine (Admin/Coordinator)")
    @PostMapping("/random")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<RandomAllocationResult> randomAllocate(
            @RequestBody(required = false) RandomAllocationRequest req,
            @RequestHeader("X-User-Id") Long coordinatorUserId) {
        if (req == null) req = new RandomAllocationRequest();
        return ResponseEntity.ok(allocationService.randomAllocate(req, coordinatorUserId));
    }

    @Operation(summary = "List all allocations, optionally filtered by status")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<PagedResponse<AllocationResponse>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) AllocationStatus status) {
        return ResponseEntity.ok(allocationService.listAll(page, size, status));
    }

    @Operation(summary = "Get a mentor's active student list")
    @GetMapping("/mentor/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','MENTOR')")
    public ResponseEntity<List<AllocationResponse>> getMentorStudents(
            @PathVariable Long userId) {
        return ResponseEntity.ok(allocationService.getMentorStudents(userId));
    }

    @Operation(summary = "Get a student's current active mentor")
    @GetMapping("/student/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','MENTOR','STUDENT')")
    public ResponseEntity<AllocationResponse> getStudentMentor(
            @PathVariable Long userId) {
        return ResponseEntity.ok(allocationService.getStudentMentor(userId));
    }

    @Operation(summary = "Transfer a student to a different mentor")
    @PutMapping("/{id}/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<AllocationResponse> transfer(
            @PathVariable Long id,
            @Valid @RequestBody TransferRequest req,
            @RequestHeader("X-User-Id") Long coordinatorUserId) {
        return ResponseEntity.ok(allocationService.transfer(id, req, coordinatorUserId));
    }

    @Operation(summary = "Deactivate an allocation")
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<AllocationResponse> deactivate(
            @PathVariable Long id,
            @RequestBody(required = false) DeactivateRequest req) {
        if (req == null) req = new DeactivateRequest();
        return ResponseEntity.ok(allocationService.deactivate(id, req));
    }

    @Operation(summary = "Get list of student IDs with no active allocation")
    @GetMapping("/unallocated-students")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<List<Long>> getUnallocatedStudents() {
        return ResponseEntity.ok(allocationService.getUnallocatedStudentIds());
    }
}

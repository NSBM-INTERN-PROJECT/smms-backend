package com.smms.allocation.controller;

import com.smms.allocation.dto.request.AllocationRequest;
import com.smms.allocation.dto.response.AllocationResponse;
import com.smms.allocation.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    // Mentor kenekwa assign karanna (POST Request)
    @PostMapping
    public ResponseEntity<AllocationResponse> allocateMentor(@Valid @RequestBody AllocationRequest request) {
        return new ResponseEntity<>(allocationService.allocateMentor(request), HttpStatus.CREATED);
    }

    // Okkoma allocations balanna (GET Request)
    @GetMapping
    public ResponseEntity<List<AllocationResponse>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    // Student ID eken allocations hoyanna (GET Request)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AllocationResponse>> getAllocationsByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(allocationService.getAllocationsByStudent(studentId));
    }

    // Mentor ID eken allocations hoyanna (GET Request)
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<AllocationResponse>> getAllocationsByMentor(@PathVariable String mentorId) {
        return ResponseEntity.ok(allocationService.getAllocationsByMentor(mentorId));
    }
}
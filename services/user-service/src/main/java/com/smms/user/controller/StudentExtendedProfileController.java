package com.smms.user.controller;

import com.smms.user.dto.request.StudentExtendedProfileRequest;
import com.smms.user.dto.response.StudentExtendedProfileResponse;
import com.smms.user.service.StudentExtendedProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/students")
@RequiredArgsConstructor
public class StudentExtendedProfileController {

    private final StudentExtendedProfileService extendedProfileService;

    // Student submits (first time) or requests a change (subsequent times)
    @PostMapping("/me/extended-profile")
    public ResponseEntity<StudentExtendedProfileResponse> submitExtendedProfile(
            @RequestHeader("X-User-Id") Long studentUserId,
            @RequestHeader("X-Mentor-Id") Long mentorUserId, // temporary — normally resolved via allocation-service
            @Valid @RequestBody StudentExtendedProfileRequest request) {

        StudentExtendedProfileResponse response =
                extendedProfileService.submitOrUpdateExtendedProfile(studentUserId, mentorUserId, request);
        return ResponseEntity.ok(response);
    }

    // Student views their own extended profile
    @GetMapping("/me/extended-profile")
    public ResponseEntity<StudentExtendedProfileResponse> getOwnExtendedProfile(
            @RequestHeader("X-User-Id") Long studentUserId) {

        StudentExtendedProfileResponse response =
                extendedProfileService.getOwnExtendedProfile(studentUserId);
        return ResponseEntity.ok(response);
    }
}
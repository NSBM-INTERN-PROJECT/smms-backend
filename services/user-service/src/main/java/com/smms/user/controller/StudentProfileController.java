package com.smms.user.controller;

import com.smms.user.dto.request.StudentProfileUpdateRequest;
import com.smms.user.dto.response.StudentProfileResponse;
import com.smms.user.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PutMapping("/admin/profiles/student/{userId}")
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            @PathVariable Long userId,
            @Valid @RequestBody StudentProfileUpdateRequest request) {

        StudentProfileResponse response = studentProfileService.updateStudentProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profiles/student/{userId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(
            @PathVariable Long userId) {

        StudentProfileResponse response = studentProfileService.getStudentProfileByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
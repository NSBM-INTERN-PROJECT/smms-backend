package com.smms.user.controller;

import com.smms.user.dto.request.MentorProfileUpdateRequest;
import com.smms.user.dto.response.MentorProfileResponse;
import com.smms.user.service.MentorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;

    // Admin updates a mentor's core profile
    @PutMapping("/admin/profiles/mentor/{userId}")
    public ResponseEntity<MentorProfileResponse> updateMentorProfile(
            @PathVariable Long userId,
            @Valid @RequestBody MentorProfileUpdateRequest request) {

        MentorProfileResponse response = mentorProfileService.updateMentorProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    // Any role can view a mentor's profile by userId (used internally / by mentor themself)
    @GetMapping("/profiles/mentor/{userId}")
    public ResponseEntity<MentorProfileResponse> getMentorProfile(
            @PathVariable Long userId) {

        MentorProfileResponse response = mentorProfileService.getMentorProfileByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
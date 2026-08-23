package com.smms.user.controller;

import com.smms.user.dto.request.ProfileUpdateRejectRequest;
import com.smms.user.dto.response.ProfileUpdateRequestResponse;
import com.smms.user.service.ProfileUpdateRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/profile-requests")
@RequiredArgsConstructor
public class ProfileUpdateRequestController {

    private final ProfileUpdateRequestService profileUpdateRequestService;

    // Mentor views their pending approval requests
    @GetMapping("/pending")
    public ResponseEntity<List<ProfileUpdateRequestResponse>> getPendingRequests(
            @RequestHeader("X-User-Id") Long mentorUserId) {

        List<ProfileUpdateRequestResponse> response =
                profileUpdateRequestService.getPendingRequestsForMentor(mentorUserId);
        return ResponseEntity.ok(response);
    }

    // Mentor approves a change request
    @PutMapping("/{id}/approve")
    public ResponseEntity<ProfileUpdateRequestResponse> approveRequest(
            @PathVariable Long id) {

        ProfileUpdateRequestResponse response = profileUpdateRequestService.approveRequest(id);
        return ResponseEntity.ok(response);
    }

    // Mentor rejects a change request (with a mandatory reason)
    @PutMapping("/{id}/reject")
    public ResponseEntity<ProfileUpdateRequestResponse> rejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRejectRequest rejectRequest) {

        ProfileUpdateRequestResponse response =
                profileUpdateRequestService.rejectRequest(id, rejectRequest);
        return ResponseEntity.ok(response);
    }
}
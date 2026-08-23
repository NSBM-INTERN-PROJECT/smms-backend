package com.smms.user.controller;

import com.smms.user.dto.request.DataCollectionRequestCreate;
import com.smms.user.dto.response.DataCollectionRequestResponse;
import com.smms.user.service.DataCollectionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/data-requests")
@RequiredArgsConstructor
public class DataCollectionRequestController {

    private final DataCollectionRequestService dataCollectionRequestService;

    // Mentor sends a form-fill request to a filtered group of students
    @PostMapping
    public ResponseEntity<DataCollectionRequestResponse> createRequest(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @RequestBody DataCollectionRequestCreate request) {

        // TODO: replace with a real query against StudentProfile matching department/degree/batch
        // AND extended profile form_submitted = false. Mocked for now until DB is connected.
        List<Long> matchedStudentIds = List.of();

        DataCollectionRequestResponse response =
                dataCollectionRequestService.createRequest(mentorUserId, request, matchedStudentIds);
        return ResponseEntity.ok(response);
    }

    // Mentor views all requests they've sent
    @GetMapping
    public ResponseEntity<List<DataCollectionRequestResponse>> getMyRequests(
            @RequestHeader("X-User-Id") Long mentorUserId) {

        List<DataCollectionRequestResponse> response =
                dataCollectionRequestService.getMyRequests(mentorUserId);
        return ResponseEntity.ok(response);
    }

    // Mentor sees response status per student for a specific request
    @GetMapping("/{id}/recipients")
    public ResponseEntity<DataCollectionRequestResponse> getRequestStatus(
            @PathVariable Long id) {

        DataCollectionRequestResponse response =
                dataCollectionRequestService.getRequestWithRecipientStatus(id);
        return ResponseEntity.ok(response);
    }
}
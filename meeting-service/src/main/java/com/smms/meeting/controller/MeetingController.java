package com.smms.meeting.controller;

import com.smms.meeting.dto.request.MeetingRequest;
import com.smms.meeting.dto.response.MeetingResponse;
import com.smms.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    // Create a new meeting
    @PostMapping
    public ResponseEntity<MeetingResponse> scheduleMeeting(@Valid @RequestBody MeetingRequest request) {
        return new ResponseEntity<>(meetingService.scheduleMeeting(request), HttpStatus.CREATED);
    }

    // Get all meetings
    @GetMapping
    public ResponseEntity<List<MeetingResponse>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    // Get meetings by allocation ID
    @GetMapping("/allocation/{allocationId}")
    public ResponseEntity<List<MeetingResponse>> getMeetingsByAllocationId(@PathVariable Long allocationId) {
        return ResponseEntity.ok(meetingService.getMeetingsByAllocationId(allocationId));
    }
}
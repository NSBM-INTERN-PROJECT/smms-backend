package com.smms.session.controller;

import com.smms.session.dto.request.SessionRequest;
import com.smms.session.dto.response.SessionResponse;
import com.smms.session.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    // Create a new session record
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest request) {
        return new ResponseEntity<>(sessionService.createSession(request), HttpStatus.CREATED);
    }

    // Get all sessions
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    // Get session by meeting ID
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<SessionResponse> getSessionByMeetingId(@PathVariable Long meetingId) {
        return ResponseEntity.ok(sessionService.getSessionByMeetingId(meetingId));
    }
}
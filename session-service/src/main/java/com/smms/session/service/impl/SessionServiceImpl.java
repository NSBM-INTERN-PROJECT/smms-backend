package com.smms.session.service.impl;

import com.smms.session.dto.request.SessionRequest;
import com.smms.session.dto.response.SessionResponse;
import com.smms.session.entity.Session;
import com.smms.session.repository.SessionRepository;
import com.smms.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public SessionResponse createSession(SessionRequest request) {
        // Map request to entity
        Session session = Session.builder()
                .meetingId(request.getMeetingId())
                .notes(request.getNotes())
                .attendanceStatus(request.getAttendanceStatus())
                .build();

        // Save to database
        Session savedSession = sessionRepository.save(session);

        return mapToResponse(savedSession);
    }

    @Override
    public List<SessionResponse> getAllSessions() {
        // Get all sessions
        return sessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SessionResponse getSessionByMeetingId(Long meetingId) {
        // Find session by meeting ID
        Session session = sessionRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        return mapToResponse(session);
    }

    // Convert entity to response DTO
    private SessionResponse mapToResponse(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .meetingId(session.getMeetingId())
                .notes(session.getNotes())
                .attendanceStatus(session.getAttendanceStatus())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
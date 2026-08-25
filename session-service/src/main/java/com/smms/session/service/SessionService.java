package com.smms.session.service;

import com.smms.session.dto.request.SessionRequest;
import com.smms.session.dto.response.SessionResponse;
import java.util.List;

public interface SessionService {
    SessionResponse createSession(SessionRequest request);
    List<SessionResponse> getAllSessions();
    SessionResponse getSessionByMeetingId(Long meetingId);
}
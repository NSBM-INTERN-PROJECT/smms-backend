package com.smms.meeting_service.exception;
import org.springframework.http.HttpStatus;

public class MeetingNotFoundException extends MeetingException {
    public MeetingNotFoundException(Long id) {
        super("MEETING_NOT_FOUND", "Meeting not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

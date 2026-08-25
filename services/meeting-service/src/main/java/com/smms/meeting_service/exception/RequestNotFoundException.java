package com.smms.meeting_service.exception;
import org.springframework.http.HttpStatus;

public class RequestNotFoundException extends MeetingException {
    public RequestNotFoundException(Long id) {
        super("REQUEST_NOT_FOUND", "Meeting request not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

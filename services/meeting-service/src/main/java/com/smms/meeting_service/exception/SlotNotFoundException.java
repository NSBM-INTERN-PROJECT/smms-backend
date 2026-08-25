package com.smms.meeting_service.exception;
import org.springframework.http.HttpStatus;

public class SlotNotFoundException extends MeetingException {
    public SlotNotFoundException(Long id) {
        super("SLOT_NOT_FOUND", "Meeting slot not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

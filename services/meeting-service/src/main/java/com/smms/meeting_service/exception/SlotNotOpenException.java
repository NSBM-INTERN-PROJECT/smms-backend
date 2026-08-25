package com.smms.meeting_service.exception;
import org.springframework.http.HttpStatus;

public class SlotNotOpenException extends MeetingException {
    public SlotNotOpenException(Long slotId) {
        super("SLOT_NOT_OPEN", "Slot " + slotId + " is not open for booking.", HttpStatus.CONFLICT);
    }
}

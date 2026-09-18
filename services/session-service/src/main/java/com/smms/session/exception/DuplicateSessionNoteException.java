package com.smms.session.exception;
import org.springframework.http.HttpStatus;
public class DuplicateSessionNoteException extends SessionException {
    public DuplicateSessionNoteException(Long meetingId) {
        super("DUPLICATE_SESSION_NOTE", "A session note already exists for meetingId: " + meetingId, HttpStatus.CONFLICT);
    }
}

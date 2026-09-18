package com.smms.session.exception;
import org.springframework.http.HttpStatus;
public class SessionNoteNotFoundException extends SessionException {
    public SessionNoteNotFoundException(Long id) {
        super("SESSION_NOTE_NOT_FOUND", "Session note not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

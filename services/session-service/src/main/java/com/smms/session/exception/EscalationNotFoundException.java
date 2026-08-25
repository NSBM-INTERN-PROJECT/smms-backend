package com.smms.session.exception;
import org.springframework.http.HttpStatus;
public class EscalationNotFoundException extends SessionException {
    public EscalationNotFoundException(Long id) {
        super("ESCALATION_NOT_FOUND", "Escalation not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

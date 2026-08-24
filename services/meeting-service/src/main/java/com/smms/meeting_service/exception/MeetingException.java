package com.smms.meeting_service.exception;
import org.springframework.http.HttpStatus;

public class MeetingException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;
    public MeetingException(String errorCode, String message, HttpStatus httpStatus) {
        super(message); this.errorCode = errorCode; this.httpStatus = httpStatus;
    }
    public String getErrorCode() { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}

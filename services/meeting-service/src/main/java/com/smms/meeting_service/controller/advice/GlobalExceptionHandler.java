package com.smms.meeting_service.controller.advice;

import com.smms.meeting_service.dto.response.ErrorResponse;
import com.smms.meeting_service.exception.MeetingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MeetingException.class)
    public ResponseEntity<ErrorResponse> handle(MeetingException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ErrorResponse.builder()
                .error(ex.getErrorCode()).message(ex.getMessage())
                .status(ex.getHttpStatus().value()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .error("VALIDATION_ERROR").message(msg)
                .status(HttpStatus.BAD_REQUEST.value()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccess(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder()
                .error("ACCESS_DENIED")
                .message("You do not have permission to access this resource")
                .status(HttpStatus.FORBIDDEN.value()).build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder()
                .error("INVALID_STATE").message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception in meeting-service", ex);
        return ResponseEntity.internalServerError().body(ErrorResponse.builder()
                .error("INTERNAL_ERROR").message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
    }
}

package com.smms.report.controller.advice;

import com.smms.report.dto.ErrorResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException ex) {
        log.error("Downstream service error: {}", ex.getMessage());
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.BAD_GATEWAY;
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .error("DOWNSTREAM_ERROR")
                .message("A downstream service returned an error: " + ex.getMessage())
                .status(status.value()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccess(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder()
                .error("ACCESS_DENIED")
                .message("You do not have permission to access this resource")
                .status(HttpStatus.FORBIDDEN.value()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception in report-service", ex);
        return ResponseEntity.internalServerError().body(ErrorResponse.builder()
                .error("INTERNAL_ERROR").message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
    }
}

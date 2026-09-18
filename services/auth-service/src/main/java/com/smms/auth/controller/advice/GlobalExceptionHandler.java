package com.smms.auth.controller.advice;

import com.smms.auth.dto.response.ErrorResponse;
import com.smms.auth.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler — maps domain exceptions to structured JSON error responses.
 * All error responses follow the shape: { error, message, status, timestamp }
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles all custom AuthException subclasses.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        log.debug("Auth domain error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(
                ErrorResponse.builder()
                        .error(ex.getErrorCode())
                        .message(ex.getMessage())
                        .status(ex.getHttpStatus().value())
                        .build());
    }

    /**
     * Handles @Valid Bean Validation failures — returns a comma-joined list of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("VALIDATION_ERROR")
                        .message(message)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    /**
     * Handles @PreAuthorize failures (e.g. non-admin accessing admin endpoint).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder()
                        .error("ACCESS_DENIED")
                        .message("You do not have permission to access this resource")
                        .status(HttpStatus.FORBIDDEN.value())
                        .build());
    }

    /**
     * Handles invalid refresh token errors from JwtService.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .error("INVALID_TOKEN")
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    /**
     * Handles missing request headers (e.g. missing X-User-Id).
     */
    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(org.springframework.web.bind.MissingRequestHeaderException ex) {
        log.warn("Missing request header: {}", ex.getHeaderName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .error("MISSING_HEADER")
                        .message("Required header '" + ex.getHeaderName() + "' is missing")
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    /**
     * Handles invalid path variables or header type mismatches.
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch for parameter [{}]: {}", ex.getName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("INVALID_PARAMETER")
                        .message("Invalid value for parameter: " + ex.getName())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    /**
     * Catch-all for unexpected errors — returns 500 without leaking internals.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception in auth-service: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .error("INTERNAL_ERROR")
                        .message(ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "An unexpected error occurred")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }
}

package com.smms.report.exception;

import com.smms.report.dto.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                details.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                "Input validation failed", details);
    }

    @ExceptionHandler(InvalidExportFormatException.class)
    public ResponseEntity<ApiError> handleInvalidExportFormat(InvalidExportFormatException exception) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_EXPORT_FORMAT", exception.getMessage(), null);
    }

    @ExceptionHandler(InvalidDownstreamDataException.class)
    public ResponseEntity<ApiError> handleInvalidDownstreamData(InvalidDownstreamDataException exception) {
        return build(HttpStatus.BAD_GATEWAY, "INVALID_DOWNSTREAM_DATA", exception.getMessage(), null);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ApiError> handleDownstreamService(DownstreamServiceException exception) {
        Map<String, String> details = Map.of(
                "service", exception.getServiceName(),
                "downstreamStatus", String.valueOf(exception.getDownstreamStatus()));
        return build(HttpStatus.SERVICE_UNAVAILABLE, "DOWNSTREAM_SERVICE_UNAVAILABLE",
                exception.getMessage(), details);
    }

    @ExceptionHandler(ReportExportException.class)
    public ResponseEntity<ApiError> handleExportFailure(ReportExportException exception) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT_EXPORT_FAILED",
                exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred", null);
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String error,
            String message,
            Map<String, String> details
    ) {
        return ResponseEntity.status(status).body(new ApiError(
                status.value(), error, message, details, Instant.now()));
    }
}

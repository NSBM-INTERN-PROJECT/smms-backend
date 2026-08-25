package com.smms.report.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ErrorResponse {
    private String error;
    private String message;
    @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
}

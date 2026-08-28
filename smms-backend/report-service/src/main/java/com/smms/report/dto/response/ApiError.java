package com.smms.report.dto.response;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        int status,
        String error,
        String message,
        Map<String, String> details,
        Instant timestamp
) {
}

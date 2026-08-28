package com.smms.report.dto.response;

public record ExportedFile(
        byte[] content,
        String contentType,
        String fileName
) {
}

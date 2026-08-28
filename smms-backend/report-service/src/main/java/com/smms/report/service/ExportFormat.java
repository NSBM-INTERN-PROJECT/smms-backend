package com.smms.report.service;

import com.smms.report.exception.InvalidExportFormatException;

import java.util.Locale;

public enum ExportFormat {
    CSV,
    EXCEL,
    PDF;

    public static ExportFormat fromPathValue(String value) {
        if (value == null) {
            throw new InvalidExportFormatException("Export format is required");
        }

        try {
            return ExportFormat.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidExportFormatException(
                    "Unsupported export format. Allowed values are csv, excel, and pdf");
        }
    }
}

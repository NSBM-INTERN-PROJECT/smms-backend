package com.smms.report.dto;

import lombok.Builder;
import lombok.Data;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic key-value row for CSV/Excel/PDF export.
 * Keys become column headers; values are the cell data.
 * Using LinkedHashMap to preserve column order.
 */
@Data @Builder
public class ExportRow {
    @Builder.Default
    private Map<String, String> columns = new LinkedHashMap<>();
}

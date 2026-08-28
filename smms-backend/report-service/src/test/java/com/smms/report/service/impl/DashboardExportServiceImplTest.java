package com.smms.report.service.impl;

import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.ExportedFile;
import com.smms.report.service.ExportFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardExportServiceImplTest {

    private final DashboardExportServiceImpl service = new DashboardExportServiceImpl();

    @Test
    void export_whenFormatIsCsv_returnsValidCsvFile() {
        ExportedFile result = service.export(dashboard(), ExportFormat.CSV);

        String content = new String(result.content(), StandardCharsets.UTF_8);
        assertThat(result.contentType()).isEqualTo("text/csv");
        assertThat(result.fileName()).endsWith(".csv");
        assertThat(content).contains("Metric,Value", "Total Students", "120");
    }

    @Test
    void export_whenFormatIsExcel_returnsReadableWorkbook() throws Exception {
        ExportedFile result = service.export(dashboard(), ExportFormat.EXCEL);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result.content()))) {
            assertThat(workbook.getSheet("Dashboard Summary")).isNotNull();
            assertThat(workbook.getSheet("Dashboard Summary").getRow(1).getCell(1).getStringCellValue())
                    .isEqualTo("120");
        }
    }

    @Test
    void export_whenFormatIsPdf_returnsPdfFile() {
        ExportedFile result = service.export(dashboard(), ExportFormat.PDF);

        String signature = new String(result.content(), 0, 4, StandardCharsets.ISO_8859_1);
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(result.fileName()).endsWith(".pdf");
        assertThat(signature).isEqualTo("%PDF");
    }

    private DashboardSummaryResponse dashboard() {
        return new DashboardSummaryResponse(
                120, 15, 110, 10, 105, 85, 20,
                new BigDecimal("87.5"), 8, 3,
                Instant.parse("2026-08-13T10:15:30Z"));
    }
}

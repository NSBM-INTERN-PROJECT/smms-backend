package com.smms.report.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.ExportedFile;
import com.smms.report.exception.ReportExportException;
import com.smms.report.service.DashboardExportService;
import com.smms.report.service.ExportFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DashboardExportServiceImpl implements DashboardExportService {

    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String EXCEL_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

    @Override
    public ExportedFile export(DashboardSummaryResponse dashboard, ExportFormat format) {
        return switch (format) {
            case CSV -> new ExportedFile(exportCsv(dashboard), CSV_CONTENT_TYPE, fileName(dashboard, "csv"));
            case EXCEL -> new ExportedFile(
                    exportExcel(dashboard), EXCEL_CONTENT_TYPE, fileName(dashboard, "xlsx"));
            case PDF -> new ExportedFile(exportPdf(dashboard), PDF_CONTENT_TYPE, fileName(dashboard, "pdf"));
        };
    }

    private byte[] exportCsv(DashboardSummaryResponse dashboard) {
        StringBuilder csv = new StringBuilder("Metric,Value\n");
        rows(dashboard).forEach(row -> csv
                .append(csvCell(row.label()))
                .append(',')
                .append(csvCell(row.value()))
                .append('\n'));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] exportExcel(DashboardSummaryResponse dashboard) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Dashboard Summary");
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
            headerStyle.setFont(headerFont);

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Metric");
            header.createCell(1).setCellValue("Value");
            header.getCell(0).setCellStyle(headerStyle);
            header.getCell(1).setCellStyle(headerStyle);

            List<MetricRow> metricRows = rows(dashboard);
            for (int index = 0; index < metricRows.size(); index++) {
                MetricRow metric = metricRows.get(index);
                Row row = sheet.createRow(index + 1);
                row.createCell(0).setCellValue(metric.label());
                row.createCell(1).setCellValue(metric.value());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new ReportExportException("Unable to generate Excel report", exception);
        }
    }

    private byte[] exportPdf(DashboardSummaryResponse dashboard) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, output);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("SMMS Dashboard Summary", titleFont));
            document.add(new Paragraph("Generated at: " + dashboard.generatedAt()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2});
            table.addCell(headerCell("Metric"));
            table.addCell(headerCell("Value"));

            rows(dashboard).forEach(row -> {
                table.addCell(row.label());
                table.addCell(row.value());
            });
            document.add(table);
        } catch (DocumentException exception) {
            throw new ReportExportException("Unable to generate PDF report", exception);
        } finally {
            document.close();
        }

        return output.toByteArray();
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
        cell.setBackgroundColor(new Color(37, 99, 235));
        cell.setPadding(7);
        return cell;
    }

    private List<MetricRow> rows(DashboardSummaryResponse dashboard) {
        return List.of(
                new MetricRow("Total Students", String.valueOf(dashboard.totalStudents())),
                new MetricRow("Total Mentors", String.valueOf(dashboard.totalMentors())),
                new MetricRow("Allocated Students", String.valueOf(dashboard.allocatedStudents())),
                new MetricRow("Unallocated Students", String.valueOf(dashboard.unallocatedStudents())),
                new MetricRow("Total Meetings", String.valueOf(dashboard.totalMeetings())),
                new MetricRow("Completed Meetings", String.valueOf(dashboard.completedMeetings())),
                new MetricRow("Pending Meetings", String.valueOf(dashboard.pendingMeetings())),
                new MetricRow("Attendance Rate (%)", decimalText(dashboard.attendanceRate())),
                new MetricRow("At-Risk Students", String.valueOf(dashboard.atRiskStudents())),
                new MetricRow("Open Escalations", String.valueOf(dashboard.openEscalations())),
                new MetricRow("Generated At", dashboard.generatedAt().toString()));
    }

    private String decimalText(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private String csvCell(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private String fileName(DashboardSummaryResponse dashboard, String extension) {
        return "smms-dashboard-" + FILE_TIMESTAMP.format(dashboard.generatedAt()) + "." + extension;
    }

    private record MetricRow(String label, String value) {
    }
}

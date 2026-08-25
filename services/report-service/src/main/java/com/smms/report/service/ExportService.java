package com.smms.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smms.report.client.AllocationServiceClient;
import com.smms.report.client.SessionServiceClient;
import com.smms.report.client.UserServiceClient;
import com.smms.report.client.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class ExportService {

    private final UserServiceClient userClient;
    private final AllocationServiceClient allocationClient;
    private final SessionServiceClient sessionClient;

    // ─── CSV ──────────────────────────────────────────────────────────────

    public void writeStudentProgressCsv(PrintWriter writer, String batch, String dept,
                                         Long adminUserId, String role) throws IOException {
        List<StudentSummaryDto> students = userClient.getActiveStudents(batch, dept);
        writer.println("StudentId,Name,Email,Batch,Department,LatestProgress,OpenEscalations,LastNoteAt");
        for (StudentSummaryDto s : students) {
            try {
                PagedResponseDto<SessionNoteDto> notePage =
                        sessionClient.getStudentNotes(s.getUserId(), adminUserId, role, 0, 1);
                String progress = notePage.getContent() != null && !notePage.getContent().isEmpty()
                        ? notePage.getContent().get(0).getProgressStatus() : "NO_NOTES";
                PagedResponseDto<EscalationDto> escPage =
                        sessionClient.getStudentEscalations(s.getUserId(), adminUserId, role, 0, 100);
                long openEsc = escPage.getContent() != null
                        ? escPage.getContent().stream().filter(e -> "OPEN".equals(e.getStatus())).count() : 0;
                writer.printf("%d,\"%s\",\"%s\",%s,%s,%s,%d,%n",
                        s.getUserId(), s.getFullName(), s.getEmail(),
                        s.getBatch(), s.getDepartment(), progress, openEsc);
            } catch (Exception e) {
                log.warn("Error exporting student {}: {}", s.getUserId(), e.getMessage());
            }
        }
        writer.flush();
    }

    public void writeAllocationCsv(PrintWriter writer, String status) throws IOException {
        PagedResponseDto<AllocationDto> page = allocationClient.listAll(0, 2000, status);
        List<AllocationDto> allocs = page.getContent() != null ? page.getContent() : List.of();
        writer.println("AllocationId,MentorId,StudentId,Type,Status,AllocationDate");
        allocs.forEach(a -> writer.printf("%d,%d,%d,%s,%s,%s%n",
                a.getId(), a.getMentorUserId(), a.getStudentUserId(),
                a.getAllocationType(), a.getStatus(),
                a.getAllocationDate() != null ? a.getAllocationDate().toString() : ""));
        writer.flush();
    }

    public void writeEscalationCsv(PrintWriter writer, Long adminUserId, String role,
                                    String status, String category) throws IOException {
        PagedResponseDto<EscalationDto> page =
                sessionClient.listAllEscalations(adminUserId, role, 0, 2000, status, category);
        List<EscalationDto> escs = page.getContent() != null ? page.getContent() : List.of();
        writer.println("EscalationId,MentorId,StudentId,Category,Status,EscalatedTo,CreatedAt,ResolvedAt");
        escs.forEach(e -> writer.printf("%d,%d,%d,%s,%s,%s,%s,%s%n",
                e.getId(), e.getMentorUserId(), e.getStudentUserId(),
                e.getCategory(), e.getStatus(), e.getEscalatedToRole(),
                e.getCreatedAt(), e.getResolvedAt() != null ? e.getResolvedAt() : ""));
        writer.flush();
    }

    // ─── Excel ────────────────────────────────────────────────────────────

    public byte[] buildStudentProgressExcel(String batch, String dept,
                                             Long adminUserId, String role) throws IOException {
        List<StudentSummaryDto> students = userClient.getActiveStudents(batch, dept);
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Student Progress");
            String[] headers = {"StudentId","Name","Email","Batch","Department","Progress","OpenEscalations"};
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) hRow.createCell(i).setCellValue(headers[i]);

            int rowIdx = 1;
            for (StudentSummaryDto s : students) {
                try {
                    PagedResponseDto<SessionNoteDto> notePage =
                            sessionClient.getStudentNotes(s.getUserId(), adminUserId, role, 0, 1);
                    String progress = notePage.getContent() != null && !notePage.getContent().isEmpty()
                            ? notePage.getContent().get(0).getProgressStatus() : "NO_NOTES";
                    PagedResponseDto<EscalationDto> escPage =
                            sessionClient.getStudentEscalations(s.getUserId(), adminUserId, role, 0, 100);
                    long openEsc = escPage.getContent() != null
                            ? escPage.getContent().stream().filter(e -> "OPEN".equals(e.getStatus())).count() : 0;
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(s.getUserId());
                    row.createCell(1).setCellValue(s.getFullName());
                    row.createCell(2).setCellValue(s.getEmail());
                    row.createCell(3).setCellValue(s.getBatch());
                    row.createCell(4).setCellValue(s.getDepartment());
                    row.createCell(5).setCellValue(progress);
                    row.createCell(6).setCellValue(openEsc);
                } catch (Exception e) {
                    log.warn("Excel: error for student {}: {}", s.getUserId(), e.getMessage());
                }
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        }
    }

    public byte[] buildAllocationExcel(String status) throws IOException {
        PagedResponseDto<AllocationDto> page = allocationClient.listAll(0, 2000, status);
        List<AllocationDto> allocs = page.getContent() != null ? page.getContent() : List.of();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Allocations");
            String[] headers = {"ID","MentorId","StudentId","Type","Status","Date"};
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) hRow.createCell(i).setCellValue(headers[i]);
            int rowIdx = 1;
            for (AllocationDto a : allocs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(a.getMentorUserId());
                row.createCell(2).setCellValue(a.getStudentUserId());
                row.createCell(3).setCellValue(a.getAllocationType());
                row.createCell(4).setCellValue(a.getStatus());
                row.createCell(5).setCellValue(a.getAllocationDate() != null ? a.getAllocationDate().toString() : "");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        }
    }

    // ─── PDF ──────────────────────────────────────────────────────────────

    public byte[] buildEscalationPdf(Long adminUserId, String role,
                                      String status, String category) throws IOException {
        PagedResponseDto<EscalationDto> page =
                sessionClient.listAllEscalations(adminUserId, role, 0, 2000, status, category);
        List<EscalationDto> escs = page.getContent() != null ? page.getContent() : List.of();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, bos);
        doc.open();

        doc.add(new Paragraph("SMMS — Escalation Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        doc.add(new Paragraph("Generated: " + java.time.LocalDateTime.now()));
        doc.add(Chunk.NEWLINE);

        String[] cols = {"ID","MentorId","StudentId","Category","Status","EscalatedTo","CreatedAt"};
        PdfPTable table = new PdfPTable(cols.length);
        table.setWidthPercentage(100);
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            table.addCell(cell);
        }
        for (EscalationDto e : escs) {
            table.addCell(String.valueOf(e.getId()));
            table.addCell(String.valueOf(e.getMentorUserId()));
            table.addCell(String.valueOf(e.getStudentUserId()));
            table.addCell(e.getCategory());
            table.addCell(e.getStatus());
            table.addCell(e.getEscalatedToRole());
            table.addCell(e.getCreatedAt() != null ? e.getCreatedAt().toString() : "");
        }
        doc.add(table);
        doc.close();
        return bos.toByteArray();
    }
}

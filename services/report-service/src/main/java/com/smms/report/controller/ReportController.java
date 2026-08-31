package com.smms.report.controller;

import com.smms.report.dto.*;
import com.smms.report.service.DashboardService;
import com.smms.report.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Report Service", description = "Dashboard KPIs and data export (CSV, Excel, PDF)")
public class ReportController {

    private final DashboardService dashboardService;
    private final ExportService exportService;

    // ─── Dashboards ───────────────────────────────────────────────────────

    @Operation(summary = "Admin/Coordinator dashboard — aggregated KPIs across all services")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<DashboardStats> getAdminDashboard(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(dashboardService.getAdminDashboard(userId, role));
    }

    @Operation(summary = "Mentor dashboard — own students, meetings, and progress breakdown")
    @GetMapping("/dashboard/mentor")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MentorDashboard> getMentorDashboard(
            @RequestHeader("X-User-Id") Long mentorUserId) {
        return ResponseEntity.ok(dashboardService.getMentorDashboard(mentorUserId));
    }

    @Operation(summary = "Student dashboard — own meeting and progress stats")
    @GetMapping("/dashboard/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDashboard> getStudentDashboard(
            @RequestHeader("X-User-Id") Long studentUserId) {
        return ResponseEntity.ok(dashboardService.getStudentDashboard(studentUserId));
    }

    @Operation(summary = "FR-005: Advanced mentor-student filtering view (Coordinator/Admin)")
    @PostMapping("/dashboard/mentor/students")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<List<MentorStudentView>> getMentorStudentView(
            @RequestBody(required = false) MentorFilterRequest filter) {
        return ResponseEntity.ok(dashboardService.getMentorStudentView(
                filter != null ? filter : new MentorFilterRequest()));
    }

    // ─── CSV Exports ──────────────────────────────────────────────────────

    @Operation(summary = "Export student progress report as CSV")
    @GetMapping("/reports/students/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public void exportStudentsCsv(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String department,
            HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=student-progress.csv");
        exportService.writeStudentProgressCsv(response.getWriter(), batch, department, userId, role);
    }

    @Operation(summary = "Export allocation data as CSV")
    @GetMapping("/reports/allocations/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public void exportAllocationsCsv(
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=allocations.csv");
        exportService.writeAllocationCsv(response.getWriter(), status);
    }

    @Operation(summary = "Export escalation report as CSV")
    @GetMapping("/reports/escalations/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public void exportEscalationsCsv(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=escalations.csv");
        exportService.writeEscalationCsv(response.getWriter(), userId, role, status, category);
    }

    // ─── Excel Exports ────────────────────────────────────────────────────

    @Operation(summary = "Export student progress report as Excel (.xlsx)")
    @GetMapping("/reports/students/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<byte[]> exportStudentsExcel(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String department) throws IOException {
        byte[] data = exportService.buildStudentProgressExcel(batch, department, userId, role);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header("Content-Disposition", "attachment; filename=student-progress.xlsx")
                .body(data);
    }

    @Operation(summary = "Export allocation data as Excel (.xlsx)")
    @GetMapping("/reports/allocations/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<byte[]> exportAllocationsExcel(
            @RequestParam(required = false) String status) throws IOException {
        byte[] data = exportService.buildAllocationExcel(status);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header("Content-Disposition", "attachment; filename=allocations.xlsx")
                .body(data);
    }

    // ─── PDF Exports ──────────────────────────────────────────────────────

    @Operation(summary = "Export escalation report as PDF")
    @GetMapping("/reports/escalations/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public ResponseEntity<byte[]> exportEscalationsPdf(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) throws IOException {
        byte[] data = exportService.buildEscalationPdf(userId, role, status, category);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=escalations.pdf")
                .body(data);
    }
}

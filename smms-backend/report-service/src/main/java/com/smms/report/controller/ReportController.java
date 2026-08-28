package com.smms.report.controller;

import com.smms.report.dto.response.ApiError;
import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.ExportedFile;
import com.smms.report.service.DashboardReportService;
import com.smms.report.service.ExportFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "SMMS dashboard analytics and exports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final DashboardReportService dashboardReportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Get the owner dashboard summary")
    @ApiResponse(responseCode = "200", description = "Dashboard generated successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "OWNER role required",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "503", description = "A required service is unavailable",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(dashboardReportService.getDashboardSummary());
    }

    @GetMapping("/dashboard/exports/{format}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Export the owner dashboard as CSV, Excel, or PDF")
    @ApiResponse(responseCode = "200", description = "Report exported successfully")
    @ApiResponse(responseCode = "400", description = "Unsupported export format",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "OWNER role required",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<byte[]> exportDashboard(
            @Parameter(description = "csv, excel, or pdf", example = "pdf")
            @PathVariable String format
    ) {
        ExportedFile exportedFile = dashboardReportService.exportDashboard(
                ExportFormat.fromPathValue(format));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(exportedFile.contentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(exportedFile.fileName())
                .build());
        headers.setContentLength(exportedFile.content().length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(exportedFile.content());
    }
}

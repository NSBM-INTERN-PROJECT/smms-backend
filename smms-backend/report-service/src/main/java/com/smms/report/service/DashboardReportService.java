package com.smms.report.service;

import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.ExportedFile;

public interface DashboardReportService {

    DashboardSummaryResponse getDashboardSummary();

    ExportedFile exportDashboard(ExportFormat format);
}

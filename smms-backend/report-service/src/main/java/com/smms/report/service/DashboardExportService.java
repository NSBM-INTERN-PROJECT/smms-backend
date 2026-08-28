package com.smms.report.service;

import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.ExportedFile;

public interface DashboardExportService {

    ExportedFile export(DashboardSummaryResponse dashboard, ExportFormat format);
}

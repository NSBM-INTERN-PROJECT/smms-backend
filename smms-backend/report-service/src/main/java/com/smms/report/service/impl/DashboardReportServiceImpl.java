package com.smms.report.service.impl;

import com.smms.report.client.AllocationServiceClient;
import com.smms.report.client.MeetingServiceClient;
import com.smms.report.client.SessionServiceClient;
import com.smms.report.client.UserServiceClient;
import com.smms.report.dto.response.AllocationStatisticsResponse;
import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.ExportedFile;
import com.smms.report.dto.response.MeetingStatisticsResponse;
import com.smms.report.dto.response.SessionStatisticsResponse;
import com.smms.report.dto.response.UserStatisticsResponse;
import com.smms.report.exception.DownstreamServiceException;
import com.smms.report.exception.InvalidDownstreamDataException;
import com.smms.report.service.DashboardExportService;
import com.smms.report.service.DashboardReportService;
import com.smms.report.service.ExportFormat;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DashboardReportServiceImpl implements DashboardReportService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final UserServiceClient userServiceClient;
    private final AllocationServiceClient allocationServiceClient;
    private final MeetingServiceClient meetingServiceClient;
    private final SessionServiceClient sessionServiceClient;
    private final DashboardExportService dashboardExportService;

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        UserStatisticsResponse users = call("user-service", userServiceClient::getStatistics);
        AllocationStatisticsResponse allocations = call(
                "allocation-service", allocationServiceClient::getStatistics);
        MeetingStatisticsResponse meetings = call("meeting-service", meetingServiceClient::getStatistics);
        SessionStatisticsResponse sessions = call("session-service", sessionServiceClient::getStatistics);

        validate(users, allocations, meetings, sessions);

        return new DashboardSummaryResponse(
                users.totalStudents(),
                users.totalMentors(),
                allocations.allocatedStudents(),
                allocations.unallocatedStudents(),
                meetings.totalMeetings(),
                meetings.completedMeetings(),
                meetings.pendingMeetings(),
                meetings.attendanceRate(),
                sessions.atRiskStudents(),
                sessions.openEscalations(),
                Instant.now());
    }

    @Override
    public ExportedFile exportDashboard(ExportFormat format) {
        return dashboardExportService.export(getDashboardSummary(), format);
    }

    private <T> T call(String serviceName, Supplier<T> request) {
        try {
            T result = request.get();
            if (result == null) {
                throw new InvalidDownstreamDataException(serviceName + " returned an empty response");
            }
            return result;
        } catch (FeignException exception) {
            throw new DownstreamServiceException(serviceName, exception.status());
        }
    }

    private void validate(
            UserStatisticsResponse users,
            AllocationStatisticsResponse allocations,
            MeetingStatisticsResponse meetings,
            SessionStatisticsResponse sessions
    ) {
        boolean hasNegativeValue = users.totalStudents() < 0
                || users.totalMentors() < 0
                || allocations.allocatedStudents() < 0
                || allocations.unallocatedStudents() < 0
                || meetings.totalMeetings() < 0
                || meetings.completedMeetings() < 0
                || meetings.pendingMeetings() < 0
                || sessions.atRiskStudents() < 0
                || sessions.openEscalations() < 0;

        if (hasNegativeValue) {
            throw new InvalidDownstreamDataException("A downstream service returned a negative statistic");
        }

        BigDecimal attendanceRate = meetings.attendanceRate();
        if (attendanceRate == null
                || attendanceRate.compareTo(BigDecimal.ZERO) < 0
                || attendanceRate.compareTo(ONE_HUNDRED) > 0) {
            throw new InvalidDownstreamDataException("Attendance rate must be between 0 and 100");
        }
    }
}

package com.smms.report.service.impl;

import com.smms.report.client.AllocationServiceClient;
import com.smms.report.client.MeetingServiceClient;
import com.smms.report.client.SessionServiceClient;
import com.smms.report.client.UserServiceClient;
import com.smms.report.dto.response.AllocationStatisticsResponse;
import com.smms.report.dto.response.DashboardSummaryResponse;
import com.smms.report.dto.response.MeetingStatisticsResponse;
import com.smms.report.dto.response.SessionStatisticsResponse;
import com.smms.report.dto.response.UserStatisticsResponse;
import com.smms.report.exception.DownstreamServiceException;
import com.smms.report.exception.InvalidDownstreamDataException;
import com.smms.report.service.DashboardExportService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardReportServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private AllocationServiceClient allocationServiceClient;
    @Mock
    private MeetingServiceClient meetingServiceClient;
    @Mock
    private SessionServiceClient sessionServiceClient;
    @Mock
    private DashboardExportService dashboardExportService;

    @InjectMocks
    private DashboardReportServiceImpl service;

    @Test
    void getDashboardSummary_whenAllServicesRespond_returnsAggregatedDashboard() {
        when(userServiceClient.getStatistics()).thenReturn(new UserStatisticsResponse(120, 15));
        when(allocationServiceClient.getStatistics()).thenReturn(new AllocationStatisticsResponse(110, 10));
        when(meetingServiceClient.getStatistics()).thenReturn(
                new MeetingStatisticsResponse(105, 85, 20, new BigDecimal("87.5")));
        when(sessionServiceClient.getStatistics()).thenReturn(new SessionStatisticsResponse(8, 3));

        DashboardSummaryResponse result = service.getDashboardSummary();

        assertThat(result.totalStudents()).isEqualTo(120);
        assertThat(result.totalMentors()).isEqualTo(15);
        assertThat(result.allocatedStudents()).isEqualTo(110);
        assertThat(result.completedMeetings()).isEqualTo(85);
        assertThat(result.attendanceRate()).isEqualByComparingTo("87.5");
        assertThat(result.atRiskStudents()).isEqualTo(8);
        assertThat(result.generatedAt()).isNotNull();
    }

    @Test
    void getDashboardSummary_whenUserServiceFails_throwsDownstreamServiceException() {
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(503);
        when(userServiceClient.getStatistics()).thenThrow(feignException);

        assertThatThrownBy(service::getDashboardSummary)
                .isInstanceOf(DownstreamServiceException.class)
                .hasMessageContaining("user-service");
    }

    @Test
    void getDashboardSummary_whenAttendanceRateIsInvalid_throwsInvalidDownstreamDataException() {
        when(userServiceClient.getStatistics()).thenReturn(new UserStatisticsResponse(120, 15));
        when(allocationServiceClient.getStatistics()).thenReturn(new AllocationStatisticsResponse(110, 10));
        when(meetingServiceClient.getStatistics()).thenReturn(
                new MeetingStatisticsResponse(105, 85, 20, new BigDecimal("120")));
        when(sessionServiceClient.getStatistics()).thenReturn(new SessionStatisticsResponse(8, 3));

        assertThatThrownBy(service::getDashboardSummary)
                .isInstanceOf(InvalidDownstreamDataException.class)
                .hasMessageContaining("between 0 and 100");
    }
}

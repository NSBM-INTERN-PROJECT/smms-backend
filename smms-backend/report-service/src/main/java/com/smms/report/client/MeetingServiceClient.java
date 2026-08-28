package com.smms.report.client;

import com.smms.report.dto.response.MeetingStatisticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "meeting-service")
public interface MeetingServiceClient {

    @GetMapping("/api/meetings/statistics")
    MeetingStatisticsResponse getStatistics();
}

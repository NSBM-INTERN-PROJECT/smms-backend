package com.smms.report.client;

import com.smms.report.dto.response.SessionStatisticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "session-service")
public interface SessionServiceClient {

    @GetMapping("/api/sessions/statistics")
    SessionStatisticsResponse getStatistics();
}

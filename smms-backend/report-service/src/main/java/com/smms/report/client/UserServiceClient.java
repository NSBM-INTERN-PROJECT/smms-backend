package com.smms.report.client;

import com.smms.report.dto.response.UserStatisticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/statistics")
    UserStatisticsResponse getStatistics();
}

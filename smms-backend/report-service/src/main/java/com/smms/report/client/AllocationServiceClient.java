package com.smms.report.client;

import com.smms.report.dto.response.AllocationStatisticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "allocation-service")
public interface AllocationServiceClient {

    @GetMapping("/api/allocations/statistics")
    AllocationStatisticsResponse getStatistics();
}

package com.smms.report.client;

import com.smms.report.client.dto.AllocationDto;
import com.smms.report.client.dto.PagedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "allocation-service")
public interface AllocationServiceClient {

    @GetMapping("/api/allocations")
    PagedResponseDto<AllocationDto> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size,
            @RequestParam(required = false) String status);

    @GetMapping("/api/allocations/mentor/{mentorId}")
    List<AllocationDto> getMentorAllocations(@PathVariable Long mentorId);

    @GetMapping("/api/allocations/unallocated-students")
    List<Long> getUnallocatedStudentIds();
}

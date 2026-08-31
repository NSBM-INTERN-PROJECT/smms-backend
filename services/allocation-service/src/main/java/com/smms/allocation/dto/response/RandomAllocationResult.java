package com.smms.allocation.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class RandomAllocationResult {
    private int totalProcessed;
    private int successCount;
    private int skippedCount;
    private List<String> skippedReasons;
    private List<AllocationResponse> allocations;
}

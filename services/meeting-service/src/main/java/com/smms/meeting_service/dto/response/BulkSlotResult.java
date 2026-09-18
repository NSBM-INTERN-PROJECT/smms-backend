package com.smms.meeting_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class BulkSlotResult {
    private int totalRequested;
    private int createdCount;
    private int assignedCount;
    private List<SlotResponse> slots;
    private List<SlotAllocationResponse> allocations;
}

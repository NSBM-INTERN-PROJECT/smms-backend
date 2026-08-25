package com.smms.meeting_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class BulkCreateSlotRequest {
    @NotEmpty(message = "At least one slot is required")
    @Valid
    private List<CreateSlotRequest> slots;

    /** Optional: student user IDs to auto-assign to these slots. If provided,
     * the system will pair each student with one slot in order. */
    private List<Long> studentUserIds;
}

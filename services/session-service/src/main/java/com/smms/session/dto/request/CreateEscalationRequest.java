package com.smms.session.dto.request;

import com.smms.session.domain.EscalationCategory;
import com.smms.session.domain.EscalationRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateEscalationRequest {

    @NotNull(message = "sessionNoteId is required")
    private Long sessionNoteId;

    @NotNull(message = "studentUserId is required")
    private Long studentUserId;

    @NotNull(message = "category is required")
    private EscalationCategory category;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "escalatedToRole is required")
    private EscalationRole escalatedToRole;

    /** Optional: specific user ID to escalate to (e.g., a coordinator's userId). */
    private Long escalatedToUserId;
}

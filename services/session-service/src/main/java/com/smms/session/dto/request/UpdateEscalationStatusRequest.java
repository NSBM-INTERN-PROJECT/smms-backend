package com.smms.session.dto.request;

import com.smms.session.domain.EscalationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateEscalationStatusRequest {

    @NotNull(message = "status is required")
    private EscalationStatus status;

    private String resolutionNotes;
}

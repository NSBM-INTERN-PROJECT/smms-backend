package com.smms.session.dto.request;

import com.smms.session.domain.ProgressStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateSessionNoteRequest {

    @NotNull(message = "meetingId is required")
    private Long meetingId;

    @NotNull(message = "studentUserId is required")
    private Long studentUserId;

    @NotBlank(message = "Discussion notes are required")
    private String discussionNotes;

    private String actionItems;

    private ProgressStatus progressStatus = ProgressStatus.ON_TRACK;

    private LocalDate followUpDate;

    /** If true, only the mentor can view this note. Default: false. */
    private boolean isPrivate = false;
}

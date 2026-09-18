package com.smms.session.dto.request;

import com.smms.session.domain.ProgressStatus;
import lombok.Data;
import java.time.LocalDate;

/** Patch-style — only non-null fields are applied. */
@Data
public class UpdateSessionNoteRequest {
    private String discussionNotes;
    private String actionItems;
    private ProgressStatus progressStatus;
    private LocalDate followUpDate;
    private Boolean isPrivate;
}

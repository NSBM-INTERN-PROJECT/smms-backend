package com.smms.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/** Mentor approves or rejects a student profile-update request. */
@Data
public class ReviewUpdateRequest {
    @Size(max = 500)
    private String mentorNotes;
}

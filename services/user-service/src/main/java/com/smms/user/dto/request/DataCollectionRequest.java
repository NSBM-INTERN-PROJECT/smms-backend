package com.smms.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class DataCollectionRequest {

    /** Optional filter: only target students in this batch. */
    private String batch;

    /** Optional filter: only target students in this department. */
    private String department;

    /** Optional: explicit list of student user IDs (overrides batch/dept filters). */
    private List<Long> studentUserIds;

    @Size(max = 2000)
    private String message;
}

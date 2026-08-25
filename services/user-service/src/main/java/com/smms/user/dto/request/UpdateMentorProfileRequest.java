package com.smms.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Patch-style — only non-null fields are applied. */
@Data
public class UpdateMentorProfileRequest {
    @Size(max = 100)  private String fullName;
    @Size(max = 100)  private String department;
    @Size(max = 150)  private String specialization;
    @Size(max = 20)   private String phone;
    @Min(1) @Max(20)  private Integer maxStudents;
    private Boolean isActive;
}

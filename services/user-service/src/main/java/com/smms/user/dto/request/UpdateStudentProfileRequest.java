package com.smms.user.dto.request;

import com.smms.user.domain.RiskStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

/** Patch-style — only non-null fields are applied. */
@Data
public class UpdateStudentProfileRequest {
    @Size(max = 100)  private String fullName;
    @Size(max = 20)   private String phone;
    @Size(max = 100)  private String degreeProgram;
    @Size(max = 100)  private String department;
    @Size(max = 10)   private String batch;
    @Size(max = 20)   private String intake;
    @Min(1) @Max(6)   private Integer academicYear;
    private RiskStatus riskStatus;
    private Boolean isActive;
}

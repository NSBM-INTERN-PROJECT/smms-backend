package com.smms.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStudentProfileRequest {

    @NotNull  private Long userId;

    @NotBlank @Size(max = 100)  private String fullName;
    @NotBlank @Size(max = 20)   private String studentId;

    @NotBlank @Email @Size(max = 100)  private String email;

    @Size(max = 20)             private String phone;

    @NotBlank @Size(max = 100)  private String degreeProgram;
    @NotBlank @Size(max = 100)  private String department;
    @NotBlank @Size(max = 10)   private String batch;
    @NotBlank @Size(max = 20)   private String intake;

    @NotNull @Min(1) @Max(6)    private Integer academicYear;
}

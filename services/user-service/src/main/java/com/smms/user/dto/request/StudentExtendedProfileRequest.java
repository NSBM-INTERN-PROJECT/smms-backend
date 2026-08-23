package com.smms.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExtendedProfileRequest {

    @NotBlank(message = "Parent name is required")
    private String parentName;

    @NotBlank(message = "Parent phone is required")
    private String parentPhone;

    @Email(message = "Parent email must be valid")
    private String parentEmail;

    private String homeDistrict;

    @NotBlank(message = "Residence address is required")
    private String residenceAddress;

    private String emergencyContactName;

    private String emergencyContactPhone;
}
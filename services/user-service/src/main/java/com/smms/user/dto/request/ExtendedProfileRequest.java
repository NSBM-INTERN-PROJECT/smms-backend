package com.smms.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ExtendedProfileRequest {
    @Size(max = 100)  private String parentName;
    @Size(max = 20)   private String parentPhone;
    @Email @Size(max = 100)  private String parentEmail;
    @Size(max = 100)  private String homeDistrict;
    private String residenceAddress;
    @Size(max = 100)  private String emergencyContactName;
    @Size(max = 20)   private String emergencyContactPhone;
}

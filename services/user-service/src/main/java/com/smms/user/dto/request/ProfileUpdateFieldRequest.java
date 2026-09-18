package com.smms.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Student requests a change to one field in their extended profile. */
@Data
public class ProfileUpdateFieldRequest {

    @NotBlank(message = "fieldName is required")
    @Size(max = 100)
    private String fieldName;

    @NotBlank(message = "newValue is required")
    private String newValue;
}

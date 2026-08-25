package com.smms.auth.dto.request;

import com.smms.auth.domain.Role;
import com.smms.auth.domain.UserStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Patch-style update — all fields are optional.
 * Only non-null fields will be applied.
 */
@Data
public class UpdateAccountRequest {

    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    private Role role;

    private UserStatus status;
}

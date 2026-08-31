package com.smms.auth.dto.request;

import com.smms.auth.domain.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Temporary password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String temporaryPassword;
}

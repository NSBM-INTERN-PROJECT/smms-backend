package com.smms.auth.dto.response;

import com.smms.auth.domain.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresIn;       // seconds
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
    private boolean mustChangePassword;
}

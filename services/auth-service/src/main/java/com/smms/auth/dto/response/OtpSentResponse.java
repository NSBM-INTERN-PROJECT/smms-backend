package com.smms.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpSentResponse {
    private String message;
    private String email;
    private int expiresInSeconds;
    private int resendCooldownSeconds;
}

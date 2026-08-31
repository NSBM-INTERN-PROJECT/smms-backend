package com.smms.auth.controller;

import com.smms.auth.dto.request.*;
import com.smms.auth.dto.response.AuthResponse;
import com.smms.auth.dto.response.OtpSentResponse;
import com.smms.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints — all PUBLIC (no JWT required).
 * The API Gateway whitelist allows /api/auth/login, /api/auth/verify-otp,
 * /api/auth/resend-otp, /api/auth/refresh to pass through without a token.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, OTP, and token management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Step 1: Submit credentials — triggers OTP email")
    @PostMapping("/login")
    public ResponseEntity<OtpSentResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @Operation(summary = "Step 2: Verify OTP — issues JWT access + refresh token")
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.verifyOtp(request, httpRequest));
    }

    @Operation(summary = "Resend OTP (rate-limited to once per 60 seconds)")
    @PostMapping("/resend-otp")
    public ResponseEntity<OtpSentResponse> resendOtp(
            @Valid @RequestBody ResendOtpRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.resendOtp(request, httpRequest));
    }

    @Operation(summary = "Refresh access token using a valid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.refresh(request, httpRequest));
    }

    @Operation(summary = "Logout — revokes the refresh token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        authService.logout(request.getRefreshToken(), httpRequest);
        return ResponseEntity.noContent().build();
    }
}

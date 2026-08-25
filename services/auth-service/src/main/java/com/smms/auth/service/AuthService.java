package com.smms.auth.service;

import com.smms.auth.domain.AuditAction;
import com.smms.auth.domain.AuditLog;
import com.smms.auth.domain.RefreshToken;
import com.smms.auth.domain.User;
import com.smms.auth.domain.UserStatus;
import com.smms.auth.dto.request.LoginRequest;
import com.smms.auth.dto.request.RefreshTokenRequest;
import com.smms.auth.dto.request.ResendOtpRequest;
import com.smms.auth.dto.request.VerifyOtpRequest;
import com.smms.auth.dto.response.AuthResponse;
import com.smms.auth.dto.response.OtpSentResponse;
import com.smms.auth.exception.InvalidCredentialsException;
import com.smms.auth.exception.AccountNotFoundException;
import com.smms.auth.repository.AuditLogRepository;
import com.smms.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Step 1 of 2-factor login.
     * Validates credentials; if correct, sends OTP to email.
     */
    @Transactional
    public OtpSentResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Check account status
        if (user.getStatus() == UserStatus.LOCKED) {
            auditLog(user.getId(), AuditAction.LOGIN_FAILED, httpRequest, "Account locked");
            throw new InvalidCredentialsException();
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            auditLog(user.getId(), AuditAction.LOGIN_FAILED, httpRequest, "Account inactive");
            throw new InvalidCredentialsException();
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            auditLog(user.getId(), AuditAction.LOGIN_FAILED, httpRequest, "Bad password");
            throw new InvalidCredentialsException();
        }

        // Send OTP
        otpService.generateAndSendOtp(user);
        auditLog(user.getId(), AuditAction.OTP_SENT, httpRequest, null);

        log.info("OTP sent to user: {}", user.getEmail());

        return OtpSentResponse.builder()
                .message("OTP sent to your registered email address")
                .email(maskEmail(user.getEmail()))
                .expiresInSeconds(300)
                .resendCooldownSeconds(60)
                .build();
    }

    /**
     * Step 2 of 2-factor login.
     * Validates OTP; on success, issues JWT access + refresh token pair.
     */
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccountNotFoundException(request.getEmail()));

        // Validate OTP (throws on bad/expired/max-attempts)
        otpService.validateOtp(user.getId(), request.getOtpCode());

        // Issue tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        auditLog(user.getId(), AuditAction.LOGIN_SUCCESS, httpRequest, null);
        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessExpirySeconds())
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()))
                .build();
    }

    /**
     * Resends OTP, subject to cooldown check.
     */
    @Transactional
    public OtpSentResponse resendOtp(ResendOtpRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccountNotFoundException(request.getEmail()));

        otpService.assertResendAllowed(user.getId());
        otpService.generateAndSendOtp(user);
        auditLog(user.getId(), AuditAction.OTP_RESENT, httpRequest, null);

        return OtpSentResponse.builder()
                .message("A new OTP has been sent to your email address")
                .email(maskEmail(user.getEmail()))
                .expiresInSeconds(300)
                .resendCooldownSeconds(60)
                .build();
    }

    /**
     * Rotates the refresh token and issues a new access token.
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        RefreshToken refreshToken = jwtService.validateRefreshToken(request.getRefreshToken());

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new AccountNotFoundException(refreshToken.getUserId()));

        // Rotate: revoke old, issue new pair
        jwtService.revokeRefreshToken(request.getRefreshToken());
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        auditLog(user.getId(), AuditAction.TOKEN_REFRESHED, httpRequest, null);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getAccessExpirySeconds())
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()))
                .build();
    }

    /**
     * Revokes the refresh token (logout).
     */
    @Transactional
    public void logout(String refreshToken, HttpServletRequest httpRequest) {
        try {
            RefreshToken rt = jwtService.validateRefreshToken(refreshToken);
            jwtService.revokeRefreshToken(refreshToken);
            auditLog(rt.getUserId(), AuditAction.LOGOUT, httpRequest, null);
        } catch (Exception e) {
            // Logout is idempotent — swallow errors
            log.debug("Logout with invalid token (already revoked or expired)");
        }
    }

    // ─── Helpers ─────────────────────────────────────────────

    private void auditLog(Long userId, AuditAction action,
                          HttpServletRequest request, String note) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .ipAddress(getClientIp(request))
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .newValue(note)
                .build();
        auditLogRepository.save(log);
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    /** Masks email for security: j***@example.com */
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return email;
        return email.charAt(0) + "***" + email.substring(at);
    }
}

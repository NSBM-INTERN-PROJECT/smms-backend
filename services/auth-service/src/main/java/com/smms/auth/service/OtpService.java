package com.smms.auth.service;

import com.smms.auth.config.AppConfig;
import com.smms.auth.domain.OtpToken;
import com.smms.auth.domain.User;
import com.smms.auth.exception.OtpCooldownException;
import com.smms.auth.exception.OtpExpiredException;
import com.smms.auth.exception.OtpMaxAttemptsException;
import com.smms.auth.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final AppConfig appConfig;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a 6-digit OTP, persists it, and sends it by email.
     * Previous OTPs for the user are deleted first.
     */
    @Transactional
    public void generateAndSendOtp(User user) {
        // Clean up previous OTPs for this user
        otpTokenRepository.deleteAllByUserId(user.getId());

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(appConfig.getOtpExpiryMinutes());

        OtpToken otpToken = OtpToken.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .otpCode(code)
                .expiresAt(expiry)
                .isUsed(false)
                .attempts(0)
                .build();

        otpTokenRepository.save(otpToken);

        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), code,
                appConfig.getOtpExpiryMinutes());

        // Also log in dev mode so local testing doesn't require real email
        log.info("OTP for {} (dev only — remove in prod): {}", user.getEmail(), code);
    }

    /**
     * Validates the OTP code for the given user.
     * Increments attempts on failure. Marks as used on success.
     */
    @Transactional
    public void validateOtp(Long userId, String code) {
        OtpToken otp = otpTokenRepository
                .findTopByUserIdAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        userId, LocalDateTime.now())
                .orElseThrow(OtpExpiredException::new);

        if (otp.getAttempts() >= appConfig.getOtpMaxAttempts()) {
            throw new OtpMaxAttemptsException();
        }

        if (!otp.getOtpCode().equals(code)) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpTokenRepository.save(otp);
            if (otp.getAttempts() >= appConfig.getOtpMaxAttempts()) {
                throw new OtpMaxAttemptsException();
            }
            throw new OtpExpiredException(); // Reuse for wrong code (don't leak which error)
        }

        otp.setIsUsed(true);
        otpTokenRepository.save(otp);
    }

    /**
     * Checks whether the cooldown period has elapsed since the last OTP was sent.
     */
    public void assertResendAllowed(Long userId) {
        otpTokenRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .ifPresent(lastOtp -> {
                    long secondsElapsed = ChronoUnit.SECONDS.between(
                            lastOtp.getCreatedAt(), LocalDateTime.now());
                    long cooldown = appConfig.getOtpResendCooldownSeconds();
                    if (secondsElapsed < cooldown) {
                        throw new OtpCooldownException(cooldown - secondsElapsed);
                    }
                });
    }

    /**
     * Scheduled cleanup: removes expired OTP tokens every 10 minutes.
     */
    @Scheduled(fixedDelay = 600_000)
    @Transactional
    public void cleanupExpiredOtps() {
        otpTokenRepository.deleteExpiredBefore(LocalDateTime.now().minusMinutes(1));
        log.debug("Cleaned up expired OTP tokens");
    }
}

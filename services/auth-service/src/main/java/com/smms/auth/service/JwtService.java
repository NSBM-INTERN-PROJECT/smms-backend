package com.smms.auth.service;

import com.smms.auth.config.AppConfig;
import com.smms.auth.domain.RefreshToken;
import com.smms.auth.domain.User;
import com.smms.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final AppConfig appConfig;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Generates a signed JWT access token (15 min) with userId, email, and role claims.
     */
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + appConfig.getJwtAccessExpiryMs());

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("fullName", user.getFullName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Persists a refresh token (UUID string) valid for 7 days.
     */
    @Transactional
    public String generateRefreshToken(User user) {
        // Revoke all previous refresh tokens for this user
        refreshTokenRepository.revokeAllByUserId(user.getId());

        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now()
                .plusSeconds(appConfig.getJwtRefreshExpiryMs() / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(tokenValue)
                .expiryDate(expiry)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    /**
     * Validates a refresh token string and returns the associated token entity.
     * Throws IllegalArgumentException if not found, revoked, or expired.
     */
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or revoked refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.revokeByToken(token);
            throw new IllegalArgumentException("Refresh token has expired. Please log in again.");
        }
        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.revokeByToken(token);
    }

    public long getAccessExpirySeconds() {
        return appConfig.getJwtAccessExpiryMs() / 1000;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = appConfig.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

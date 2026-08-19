package com.smms.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT utility for the API Gateway.
 * Validates JWT signature and extracts claims.
 * The JWT secret MUST match the one used by auth-service to sign tokens.
 */
@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
        // Build the signing key from the shared secret (64+ char hex string)
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates the JWT token and returns its claims.
     *
     * @param token the raw JWT string (without "Bearer " prefix)
     * @return Claims if valid
     * @throws JwtException if the token is expired, malformed, or has invalid signature
     */
    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the user ID from validated claims.
     */
    public String getUserId(Claims claims) {
        return claims.getSubject();
    }

    /**
     * Extracts the user role from validated claims.
     */
    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }
}

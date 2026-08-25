package com.smms.api_gateway.filter;

import com.smms.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Global JWT Authentication Filter.
 * <p>
 * Runs on EVERY request before it reaches a downstream service.
 * <p>
 * FLOW:
 * 1. Check if the request path is in the PUBLIC_PATHS whitelist → pass through without auth
 * 2. Extract the Authorization header
 * 3. Validate the JWT using JwtUtil
 * 4. On success: inject X-User-Id and X-User-Role headers, forward the request
 * 5. On failure: return 401 JSON response immediately, request never reaches the service
 * <p>
 * IMPORTANT: This filter runs in the WebFlux reactive pipeline.
 * All operations must be non-blocking.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    /**
     * Paths that do NOT require a JWT.
     * OTP endpoints are public because the user hasn't received a JWT yet.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/verify-otp",
            "/api/auth/resend-otp",
            "/actuator/health",
            "/actuator/info"
    );

    /** Filter runs first (highest priority) */
    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // OPTIONS preflight — pass through for CORS
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Public paths — skip JWT validation
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header for path: {}", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "MISSING_TOKEN",
                    "Authorization header is missing or not a Bearer token");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            Claims claims = jwtUtil.validateAndGetClaims(token);
            String userId = jwtUtil.getUserId(claims);
            String role = jwtUtil.getRole(claims);

            // Forward user identity headers to downstream services
            // Services read these headers instead of re-parsing the JWT
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build();

            log.debug("JWT valid for userId={} role={} path={}", userId, role, path);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT for path: {}", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED",
                    "Your session has expired. Please log in again.");
        } catch (JwtException e) {
            log.warn("Invalid JWT for path: {} — {}", path, e.getMessage());
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN",
                    "The provided token is invalid.");
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter for path: {}", path, e);
            return sendErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_ERROR",
                    "An unexpected error occurred during authentication.");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Writes a structured JSON error response directly from the gateway.
     * The request never reaches a downstream service when this is called.
     */
    private Mono<Void> sendErrorResponse(ServerWebExchange exchange,
                                          HttpStatus status,
                                          String errorCode,
                                          String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Add security headers
        response.getHeaders().add("X-Content-Type-Options", "nosniff");
        response.getHeaders().add("X-Frame-Options", "DENY");

        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), errorCode, message
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}

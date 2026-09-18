package com.smms.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fail-safe Java RouteLocator configuration.
 * Configures all Gateway routes programmatically in Java, ensuring
 * that route definitions are compiled into bytecode and immune to
 * YAML indentation quirks or Config Server overrides.
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .uri("lb://user-service"))
                .route("allocation-service", r -> r
                        .path("/api/v1/allocations/**")
                        .uri("lb://allocation-service"))
                .route("meeting-service", r -> r
                        .path("/api/v1/meetings/**")
                        .uri("lb://meeting-service"))
                .route("session-service-notes", r -> r
                        .path("/api/v1/sessions/**")
                        .uri("lb://session-service"))
                .route("session-service-escalations", r -> r
                        .path("/api/v1/escalations/**")
                        .uri("lb://session-service"))
                .route("report-service-dashboard", r -> r
                        .path("/api/v1/dashboard/**")
                        .uri("lb://report-service"))
                .route("report-service-reports", r -> r
                        .path("/api/v1/reports/**")
                        .uri("lb://report-service"))
                .build();
    }
}
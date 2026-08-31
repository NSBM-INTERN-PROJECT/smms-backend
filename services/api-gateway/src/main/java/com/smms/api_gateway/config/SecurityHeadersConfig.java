package com.smms.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
/**
 * Adds security response headers to every response from the gateway.
 * These headers protect against common web vulnerabilities.
 */
@Configuration
public class SecurityHeadersConfig {
    @Bean
    public GlobalFilter securityHeadersFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(
                reactor.core.publisher.Mono.fromRunnable(() -> {
                    HttpHeaders headers = exchange.getResponse().getHeaders();
                    // Prevent MIME-type sniffing
                    headers.add("X-Content-Type-Options", "nosniff");
                    // Prevent click jacking
                    headers.add("X-Frame-Options", "DENY");
                    // XSS protection (legacy browsers)
                    headers.add("X-XSS-Protection", "1; mode=block");
                    // Strict transport security (HTTPS only)
                    headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                })
        );
    }
}
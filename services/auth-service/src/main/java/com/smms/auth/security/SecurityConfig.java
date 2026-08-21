package com.smms.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for auth-service.
 *
 * JWT validation is performed by the API Gateway BEFORE requests
 * reach this service. Here we only:
 * 1. Read X-User-Id / X-User-Role headers injected by the gateway
 * 2. Set up a SecurityContext so @PreAuthorize works on admin endpoints
 * 3. Keep all /api/auth/** endpoints accessible (gateway controls routing)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final RoleHeaderAuthFilter roleHeaderAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Actuator is internal only
                .requestMatchers("/actuator/**").permitAll()
                // Swagger/OpenAPI
                .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                // All other requests permitted — gateway already validated the JWT
                .anyRequest().permitAll()
            )
            // Insert our header-based auth filter before standard auth filter
            .addFilterBefore(roleHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

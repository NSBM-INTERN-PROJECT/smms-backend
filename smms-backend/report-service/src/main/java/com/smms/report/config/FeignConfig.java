package com.smms.report.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public RequestInterceptor authorizationHeaderForwardingInterceptor() {
        return requestTemplate -> {
            if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader(AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                requestTemplate.header(AUTHORIZATION, authorization);
            }
        };
    }
}

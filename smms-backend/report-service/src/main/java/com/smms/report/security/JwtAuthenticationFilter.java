package com.smms.report.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final String secretHex;

    public JwtAuthenticationFilter(@Value("${jwt.secret:}") String secretHex) {
        this.secretHex = secretHex;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (!StringUtils.hasText(authorization)
                || !authorization.startsWith(BEARER_PREFIX)
                || !StringUtils.hasText(secretHex)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authorization.substring(BEARER_PREFIX.length());
            SecretKey key = Keys.hmacShaKeyFor(HexFormat.of().parseHex(secretHex));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            if (StringUtils.hasText(username)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, extractAuthorities(claims));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = new ArrayList<>();
        addRoles(roles, claims.get("roles"));
        addRoles(roles, claims.get("role"));

        return roles.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(role -> role.toUpperCase(Locale.ROOT))
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private void addRoles(List<String> roles, Object claimValue) {
        if (claimValue instanceof Collection<?> collection) {
            collection.stream().map(String::valueOf).forEach(roles::add);
        } else if (claimValue instanceof String roleText) {
            for (String role : roleText.split(",")) {
                roles.add(role);
            }
        }
    }
}

package com.fit.se.apigateway.infrastructure.security;

import com.fit.se.apigateway.infrastructure.security.token.TokenProvider;
import com.fit.se.apigateway.infrastructure.security.token.TokenPurpose;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> PUBLIC_URLS = List.of(
            "/error",
            "/api/v1/accounts/devices",
            "/api/v1/auth/signin",
            "/api/v1/auth/signin/request",
            "/api/v1/auth/signup",
            "/api/v1/auth/signup/request",
            "/api/v1/auth/refresh",
            "/api/v1/auth/qr/challenges/request",
            "/api/v1/otp/**",
            "/api/v1/auth/password/reset/request",
            "/api/v1/auth/password/reset/confirm",
            "/api/v1/auth/password/reset",
            "/api/v1/auth/old/signin",
            "/api/v1/auth/old/signin/verify",
            "/api/v1/auth/old/signup",
            "/api/v1/auth/old/signup/verify",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico"
    );

    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_URLS.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();

        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String subject = tokenProvider.extractSubject(jwt);

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var validation = tokenProvider.validate(TokenPurpose.ACCESS, subject, jwt);

                if (validation.valid()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    subject,
                                    null,
                                    extractAuthorities(jwt)
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    HeaderMapRequestWrapper wrappedRequest = new HeaderMapRequestWrapper(request);
                    wrappedRequest.addHeader("X-User-Id", subject);

                    filterChain.doFilter(wrappedRequest, response);
                    return;
                }
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Nếu token của bạn chưa có roles thì cứ trả về empty list.
     * Sau này nếu token có claim roles/authorities thì parse tại đây.
     */
    private List<SimpleGrantedAuthority> extractAuthorities(String jwt) {
        return Collections.emptyList();
    }
}

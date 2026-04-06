package com.fit.se.api.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long userId = parseLong(request.getHeader(RequestHeaderNames.USER_ID));
        String username = request.getHeader(RequestHeaderNames.USERNAME);
        List<String> roles = split(request.getHeader(RequestHeaderNames.ROLES));
        String traceId = defaultIfBlank(request.getHeader(RequestHeaderNames.TRACE_ID), UUID.randomUUID().toString());
        String requestId = defaultIfBlank(request.getHeader(RequestHeaderNames.REQUEST_ID), UUID.randomUUID().toString());
        RequestContextHolder.set(new RequestContext(userId, username, roles, traceId, requestId));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        RequestContextHolder.clear();
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        return Long.parseLong(value);
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) return List.of();
        return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

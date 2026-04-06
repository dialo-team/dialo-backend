package com.fit.se.api.dto.response.common;

public record ValidationErrorDetail(
        String field,
        Object rejectedValue,
        String message
) {
}

package com.fit.se.common.security.token;

public record TokenValidationResult(
    boolean valid,
    TokenStatus status,
    String subject,
    TokenPurpose purpose,
    String message
) {
    public static TokenValidationResult valid(String subject, TokenPurpose purpose) {
        return new TokenValidationResult(true, TokenStatus.ACTIVE, subject, purpose, null);
    }

    public static TokenValidationResult invalid(TokenStatus status, String message) {
        return new TokenValidationResult(false, status, null, null, message);
    }
}


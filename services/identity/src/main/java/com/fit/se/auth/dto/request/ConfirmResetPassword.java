package com.fit.se.auth.dto.request;

public record ConfirmResetPassword(
        String source,
        String type,
        String otp
) {
}


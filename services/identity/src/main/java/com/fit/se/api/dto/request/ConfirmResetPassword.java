package com.fit.se.api.dto.request;

public record ConfirmResetPassword(
        String source,
        String type,
        String otp
) {
}

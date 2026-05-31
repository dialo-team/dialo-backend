package com.fit.se.auth.dto.request;

public record ResetPasswordRequest(
        String source,
        String type
) {
}


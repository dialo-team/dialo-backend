package com.fit.se.api.dto.request;

public record ResetPasswordRequest(
        String source,
        String type
) {
}

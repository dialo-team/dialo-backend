package com.fit.se.user.dto.request;

public record OverridePrivacyRequest(
        String key,
        String decision
) {
}

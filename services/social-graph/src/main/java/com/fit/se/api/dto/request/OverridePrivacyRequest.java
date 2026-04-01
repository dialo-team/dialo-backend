package com.fit.se.api.dto.request;

public record OverridePrivacyRequest(
        String key,
        String decision
) {
}

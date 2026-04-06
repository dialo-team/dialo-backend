package com.fit.se.api.dto.response.settings;

public record MemberSettingsResponse(
        Boolean pinned,
        Boolean muted,
        Boolean hidden,
        String alias
) {
}

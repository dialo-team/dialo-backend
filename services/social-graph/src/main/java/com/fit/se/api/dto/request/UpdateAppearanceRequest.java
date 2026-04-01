package com.fit.se.api.dto.request;

public record UpdateAppearanceRequest(
        String avatar,
        String background,
        String theme
) {
}

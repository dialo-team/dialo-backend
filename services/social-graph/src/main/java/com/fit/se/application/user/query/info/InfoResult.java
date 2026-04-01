package com.fit.se.application.user.query.info;

import lombok.Builder;

@Builder
public record InfoResult(
        String id,
        String userName,
        String bio,
        String avatar,
        String background,
        String theme
) {
}
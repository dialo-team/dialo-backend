package com.fit.se.user.application.command.privacy;

import lombok.Builder;

@Builder
public record OverridePrivacyCommand(
        String current,
        String targetId,
        String key,
        String decision
) {
}
package com.fit.se.application.user.command.privacy;

import lombok.Builder;

@Builder
public record OverridePrivacyCommand(
        String current,
        String targetId,
        String key,
        String decision
) {
}
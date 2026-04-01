package com.fit.se.application.user.command.privacy;

import lombok.Builder;

@Builder
public record RemovePrivacyOverrideCommand(
        String current,
        String targetId,
        String key
) {
}
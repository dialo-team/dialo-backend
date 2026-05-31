package com.fit.se.user.application.command.privacy;

import lombok.Builder;

@Builder
public record RemovePrivacyOverrideCommand(
        String current,
        String targetId,
        String key
) {
}
package com.fit.se.domain.settings.model;

import java.util.UUID;

public record ConversationMemberSettings(
        UUID conversationId,
        Long userId,
        boolean pinned,
        boolean muted,
        boolean hidden,
        String alias,
        Long assignedLabelId
) {
}

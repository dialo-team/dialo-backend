package com.fit.se.domain.block.model;

import java.util.UUID;

public record GroupBlock(
        UUID id,
        UUID conversationId,
        Long blockedUserId,
        Long blockedBy,
        String reason
) {
}

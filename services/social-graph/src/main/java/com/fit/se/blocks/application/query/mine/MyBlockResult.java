package com.fit.se.blocks.application.query.mine;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MyBlockResult(
        List<Item> blocks
) {
    @Builder
    public record Item(
            String blockId,
            String blockedUserId,
            String blockedUserName,
            String blockedAvatar,
            String reason,
            Instant createdAt
    ) {
    }
}
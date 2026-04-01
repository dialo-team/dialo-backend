package com.fit.se.application.blocks.command.unblock;

import lombok.Builder;

@Builder
public record UnblockFriendCommand(
        String blockerId,
        String blockedId
) {
}
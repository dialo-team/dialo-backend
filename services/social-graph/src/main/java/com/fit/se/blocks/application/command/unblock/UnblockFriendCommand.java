package com.fit.se.blocks.application.command.unblock;

import lombok.Builder;

@Builder
public record UnblockFriendCommand(
        String blockerId,
        String blockedId
) {
}
package com.fit.se.blocks.application.command.block;

import lombok.Builder;

@Builder
public record BlockFriendCommand(
        String blockId,
        String blockerId,
        String blockedId,
        String reason
) {}

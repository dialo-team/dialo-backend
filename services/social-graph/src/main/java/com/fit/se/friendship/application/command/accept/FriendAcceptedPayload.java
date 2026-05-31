package com.fit.se.friendship.application.command.accept;

import lombok.Builder;

@Builder
public record FriendAcceptedPayload(
        String user1Id,
        String user2Id,
        String systemMessage
) {
}
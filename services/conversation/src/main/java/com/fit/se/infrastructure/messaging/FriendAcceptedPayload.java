package com.fit.se.infrastructure.messaging;

public record FriendAcceptedPayload(
        String user1Id,
        String user2Id,
        String systemMessage
) {
}

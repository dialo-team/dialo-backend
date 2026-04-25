package com.fit.se.application.conversation;

public record FriendshipCreatedEvent(
        String userId1,
        String userId2
) {
}
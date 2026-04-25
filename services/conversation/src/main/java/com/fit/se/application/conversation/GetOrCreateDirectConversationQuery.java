package com.fit.se.application.conversation;

public record GetOrCreateDirectConversationQuery(
        String userId1,
        String userId2
) {
}
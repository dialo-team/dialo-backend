package com.fit.se.api.dto.response.conversation;

public record ConversationCreatedResponse(
        String conversationId,
        String type,
        String status
) {
}

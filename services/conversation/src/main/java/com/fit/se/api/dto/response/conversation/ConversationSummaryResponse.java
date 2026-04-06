package com.fit.se.api.dto.response.conversation;

public record ConversationSummaryResponse(
        String conversationId,
        String type,
        String status,
        String title,
        String avatarUrl,
        Boolean pinned,
        Boolean muted,
        Boolean hidden
) {
}

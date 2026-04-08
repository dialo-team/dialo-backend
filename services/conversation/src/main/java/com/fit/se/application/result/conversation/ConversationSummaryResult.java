package com.fit.se.application.result.conversation;

import java.util.UUID;

public record ConversationSummaryResult(UUID conversationId, String type, String title) {
}

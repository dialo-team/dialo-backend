package com.fit.se.infrastructure.messaging.dto;

import java.util.UUID;

public record ConversationCreatedEvent(
        UUID conversationId,
        String type
) {
}

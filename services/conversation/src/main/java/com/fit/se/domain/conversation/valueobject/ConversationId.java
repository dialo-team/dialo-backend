package com.fit.se.domain.conversation.valueobject;

import java.util.Objects;
import java.util.UUID;

public record ConversationId(UUID value) {
    public ConversationId {
        Objects.requireNonNull(value, "conversationId must not be null");
    }

    public static ConversationId newId() {
        return new ConversationId(UUID.randomUUID());
    }
}

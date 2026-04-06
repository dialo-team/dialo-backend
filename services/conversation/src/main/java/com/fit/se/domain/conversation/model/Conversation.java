package com.fit.se.domain.conversation.model;

import java.util.UUID;

public record Conversation(UUID id, ConversationType type, ConversationStatus status) {
}

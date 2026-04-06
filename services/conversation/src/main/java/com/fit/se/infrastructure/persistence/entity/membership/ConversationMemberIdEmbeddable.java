package com.fit.se.infrastructure.persistence.entity.membership;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ConversationMemberIdEmbeddable implements Serializable {

    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(name = "user_id")
    private Long userId;

    public ConversationMemberIdEmbeddable() {
    }

    public ConversationMemberIdEmbeddable(UUID conversationId, Long userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationMemberIdEmbeddable that)) return false;
        return Objects.equals(conversationId, that.conversationId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }
}

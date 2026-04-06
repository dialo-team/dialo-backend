package com.fit.se.infrastructure.persistence.entity.join;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "join_tokens")
public class JoinTokenEntity {

    @Id
    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

package com.fit.se.infrastructure.persistence.entity.block;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "group_blocks")
public class GroupBlockEntity {

    @Id
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "blocked_user_id", nullable = false)
    private Long blockedUserId;

    @Column(name = "blocked_by", nullable = false)
    private Long blockedBy;

    @Column
    private String reason;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    public Long getBlockedUserId() { return blockedUserId; }
    public void setBlockedUserId(Long blockedUserId) { this.blockedUserId = blockedUserId; }
    public Long getBlockedBy() { return blockedBy; }
    public void setBlockedBy(Long blockedBy) { this.blockedBy = blockedBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

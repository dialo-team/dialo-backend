package com.fit.se.infrastructure.persistence.entity.conversation;

import com.fit.se.domain.conversation.aggregate.ConversationStatus;
import com.fit.se.domain.conversation.aggregate.ConversationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationStatus status;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_avatar_url")
    private String groupAvatarUrl;

    @Column(name = "approval_required")
    private boolean approvalRequired;

    @Column(name = "join_token")
    private String joinToken;

    @Embedded
    private GroupPermissionPolicyEmbeddable permissionPolicy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ConversationType getType() { return type; }
    public void setType(ConversationType type) { this.type = type; }
    public ConversationStatus getStatus() { return status; }
    public void setStatus(ConversationStatus status) { this.status = status; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getGroupAvatarUrl() { return groupAvatarUrl; }
    public void setGroupAvatarUrl(String groupAvatarUrl) { this.groupAvatarUrl = groupAvatarUrl; }
    public boolean isApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(boolean approvalRequired) { this.approvalRequired = approvalRequired; }
    public String getJoinToken() { return joinToken; }
    public void setJoinToken(String joinToken) { this.joinToken = joinToken; }
    public GroupPermissionPolicyEmbeddable getPermissionPolicy() { return permissionPolicy; }
    public void setPermissionPolicy(GroupPermissionPolicyEmbeddable permissionPolicy) { this.permissionPolicy = permissionPolicy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

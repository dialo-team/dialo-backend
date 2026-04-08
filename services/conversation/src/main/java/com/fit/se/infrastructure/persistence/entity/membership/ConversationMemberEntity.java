package com.fit.se.infrastructure.persistence.entity.membership;

import com.fit.se.domain.membership.valueobject.MemberRole;
import com.fit.se.domain.membership.valueobject.MembershipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "conversation_members")
public class ConversationMemberEntity {

    @EmbeddedId
    private ConversationMemberIdEmbeddable id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipStatus status;

    @Column(name = "joined_at")
    private Instant joinedAt;

    public ConversationMemberIdEmbeddable getId() { return id; }
    public void setId(ConversationMemberIdEmbeddable id) { this.id = id; }
    public MemberRole getRole() { return role; }
    public void setRole(MemberRole role) { this.role = role; }
    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }
    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}

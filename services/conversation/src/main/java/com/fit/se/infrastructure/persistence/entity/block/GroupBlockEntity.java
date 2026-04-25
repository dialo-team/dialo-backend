package com.fit.se.infrastructure.persistence.entity.block;

import com.fit.se.infrastructure.persistence.entity.conversation.GroupConversationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "dialo_conversation_group_block")
@Table(name = "dialo_conversation_group_block")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupBlockEntity {

    @EmbeddedId
    private GroupBlockPairKey id;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private GroupConversationEntity groupConversation;

    private String reason;

    @Column(name = "blocked_by", nullable = false)
    private UUID blockedBy;

    @CreationTimestamp
    @Column(name = "blocked_at", nullable = false, updatable = false)
    private Instant blockedAt;
}
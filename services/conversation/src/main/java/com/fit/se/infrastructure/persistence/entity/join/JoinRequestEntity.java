package com.fit.se.infrastructure.persistence.entity.join;

import com.fit.se.domain.join.aggregate.JoinMethod;
import com.fit.se.domain.join.aggregate.JoinRequestStatus;
import com.fit.se.infrastructure.persistence.entity.conversation.GroupConversationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "dialo_conversation_join_request")
@Table(name = "dialo_conversation_join_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private GroupConversationEntity groupConversation;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "answer", length = 1000)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JoinMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JoinRequestStatus status;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
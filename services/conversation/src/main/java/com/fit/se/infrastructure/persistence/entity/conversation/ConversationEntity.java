package com.fit.se.infrastructure.persistence.entity.conversation;

import com.fit.se.domain.conversation.aggregate.ConversationStatus;
import com.fit.se.domain.conversation.aggregate.ConversationType;
import com.fit.se.infrastructure.persistence.common.BaseEntity;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@Entity(name = "dialo_conversation")
@Table(name = "dialo_conversation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    private Set<ConversationMemberEntity> members;
}
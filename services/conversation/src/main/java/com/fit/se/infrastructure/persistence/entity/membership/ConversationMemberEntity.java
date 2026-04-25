package com.fit.se.infrastructure.persistence.entity.membership;

import com.fit.se.domain.membership.valueobject.MemberRole;
import com.fit.se.domain.membership.valueobject.MembershipStatus;
import com.fit.se.infrastructure.persistence.entity.PersonEntity;
import com.fit.se.infrastructure.persistence.entity.conversation.ConversationEntity;
import com.fit.se.infrastructure.persistence.entity.settings.ConversationCommonSettingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "dialo_conversation_membership")
@Table(name = "dialo_conversation_membership")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMemberEntity {

    @EmbeddedId
    private ConversationMemberPairKey id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipStatus status;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "person_id", nullable = false)
    private PersonEntity person;

    @OneToOne(mappedBy = "membership", fetch = FetchType.LAZY, optional = false)
    private ConversationCommonSettingEntity setting;
}
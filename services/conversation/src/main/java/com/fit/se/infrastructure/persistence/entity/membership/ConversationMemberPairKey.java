package com.fit.se.infrastructure.persistence.entity.membership;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ConversationMemberPairKey implements Serializable {

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;
}
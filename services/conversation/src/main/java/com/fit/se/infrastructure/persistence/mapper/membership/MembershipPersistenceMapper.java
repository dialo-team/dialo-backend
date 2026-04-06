package com.fit.se.infrastructure.persistence.mapper.membership;

import com.fit.se.domain.membership.model.ConversationMember;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberEntity;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberIdEmbeddable;

public class MembershipPersistenceMapper {

    public ConversationMemberEntity toEntity(ConversationMember member) {
        ConversationMemberEntity entity = new ConversationMemberEntity();
        entity.setId(new ConversationMemberIdEmbeddable(member.conversationId(), member.userId()));
        entity.setRole(member.role());
        entity.setStatus(member.status());
        return entity;
    }

    public ConversationMember toDomain(ConversationMemberEntity entity) {
        return new ConversationMember(
                entity.getId().getConversationId(),
                entity.getId().getUserId(),
                entity.getRole(),
                entity.getStatus()
        );
    }
}

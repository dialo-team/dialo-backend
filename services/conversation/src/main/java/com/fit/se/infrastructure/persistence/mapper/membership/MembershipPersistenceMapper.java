package com.fit.se.infrastructure.persistence.mapper.membership;

import com.fit.se.domain.membership.aggregate.ConversationMember;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberEntity;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberIdEmbeddable;

public class MembershipPersistenceMapper {

    public ConversationMemberEntity toEntity(ConversationMember member) {
        ConversationMemberEntity entity = new ConversationMemberEntity();
        entity.setId(new ConversationMemberIdEmbeddable(member.getConversationId().value(), member.getUserId().value()));
        entity.setRole(member.getRole());
        entity.setStatus(member.getStatus());
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

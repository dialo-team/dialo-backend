package com.fit.se.infrastructure.persistence.mapper.conversation;

import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.aggregate.ConversationStatus;
import com.fit.se.domain.conversation.aggregate.ConversationType;
import com.fit.se.infrastructure.persistence.entity.conversation.ConversationEntity;

public class ConversationPersistenceMapper {

    public ConversationEntity toEntity(Conversation conversation) {
        ConversationEntity entity = new ConversationEntity();
        entity.setId(conversation.id());
        entity.setType(conversation.type());
        entity.setStatus(conversation.status());
        return entity;
    }

    public Conversation toDomain(ConversationEntity entity) {
        ConversationType type = entity.getType() == null ? ConversationType.GROUP : entity.getType();
        ConversationStatus status = entity.getStatus() == null ? ConversationStatus.ACTIVE : entity.getStatus();
        return new Conversation(entity.getId(), type, status);
    }
}

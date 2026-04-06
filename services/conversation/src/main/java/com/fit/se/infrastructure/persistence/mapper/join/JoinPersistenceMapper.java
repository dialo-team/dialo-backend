package com.fit.se.infrastructure.persistence.mapper.join;

import com.fit.se.domain.join.model.JoinRequest;
import com.fit.se.infrastructure.persistence.entity.join.JoinRequestEntity;

public class JoinPersistenceMapper {

    public JoinRequestEntity toEntity(JoinRequest request) {
        JoinRequestEntity entity = new JoinRequestEntity();
        entity.setId(request.id());
        entity.setConversationId(request.conversationId());
        entity.setRequesterId(request.requesterId());
        entity.setMethod(request.method());
        entity.setStatus(request.status());
        return entity;
    }

    public JoinRequest toDomain(JoinRequestEntity entity) {
        return new JoinRequest(
                entity.getId(),
                entity.getConversationId(),
                entity.getRequesterId(),
                entity.getMethod(),
                entity.getStatus()
        );
    }
}

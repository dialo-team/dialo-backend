package com.fit.se.infrastructure.persistence.mapper.label;

import com.fit.se.domain.label.aggregate.UserConversationLabel;
import com.fit.se.infrastructure.persistence.entity.label.UserConversationLabelEntity;

public class LabelPersistenceMapper {

    public UserConversationLabelEntity toEntity(UserConversationLabel label) {
        UserConversationLabelEntity entity = new UserConversationLabelEntity();
        entity.setId(label.id());
        entity.setUserId(label.userId());
        entity.setName(label.name());
        entity.setColor(label.color());
        entity.setType(label.type());
        entity.setDeletable(label.deletable());
        return entity;
    }

    public UserConversationLabel toDomain(UserConversationLabelEntity entity) {
        return new UserConversationLabel(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getColor(),
                entity.getType(),
                entity.isDeletable()
        );
    }
}

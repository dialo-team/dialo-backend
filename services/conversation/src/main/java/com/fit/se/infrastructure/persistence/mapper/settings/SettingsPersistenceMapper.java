package com.fit.se.infrastructure.persistence.mapper.settings;

import com.fit.se.domain.settings.model.ConversationMemberSettings;
import com.fit.se.infrastructure.persistence.entity.settings.ConversationMemberSettingsEntity;
import com.fit.se.infrastructure.persistence.entity.settings.ConversationMemberSettingsIdEmbeddable;

public class SettingsPersistenceMapper {

    public ConversationMemberSettingsEntity toEntity(ConversationMemberSettings settings) {
        ConversationMemberSettingsEntity entity = new ConversationMemberSettingsEntity();
        entity.setId(new ConversationMemberSettingsIdEmbeddable(settings.conversationId(), settings.userId()));
        entity.setPinned(settings.pinned());
        entity.setMuted(settings.muted());
        entity.setHidden(settings.hidden());
        entity.setAlias(settings.alias());
        entity.setAssignedLabelId(settings.assignedLabelId());
        return entity;
    }

    public ConversationMemberSettings toDomain(ConversationMemberSettingsEntity entity) {
        return new ConversationMemberSettings(
                entity.getId().getConversationId(),
                entity.getId().getUserId(),
                entity.isPinned(),
                entity.isMuted(),
                entity.isHidden(),
                entity.getAlias(),
                entity.getAssignedLabelId()
        );
    }
}

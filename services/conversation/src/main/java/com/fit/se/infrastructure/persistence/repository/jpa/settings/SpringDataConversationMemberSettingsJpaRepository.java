package com.fit.se.infrastructure.persistence.repository.jpa.settings;

import com.fit.se.infrastructure.persistence.entity.settings.ConversationMemberSettingsEntity;
import com.fit.se.infrastructure.persistence.entity.settings.ConversationMemberSettingsIdEmbeddable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataConversationMemberSettingsJpaRepository extends JpaRepository<ConversationMemberSettingsEntity, ConversationMemberSettingsIdEmbeddable> {

    Optional<ConversationMemberSettingsEntity> findByIdConversationIdAndIdUserId(UUID conversationId, Long userId);

}

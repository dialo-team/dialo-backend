package com.fit.se.infrastructure.persistence.repository.adapter.settings;

import com.fit.se.domain.settings.model.ConversationMemberSettings;
import com.fit.se.domain.settings.repository.ConversationMemberSettingsRepository;
import com.fit.se.infrastructure.persistence.mapper.settings.SettingsPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.settings.SpringDataConversationMemberSettingsJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConversationMemberSettingsRepositoryAdapter implements ConversationMemberSettingsRepository {

    private final SpringDataConversationMemberSettingsJpaRepository repository;
    private final SettingsPersistenceMapper mapper = new SettingsPersistenceMapper();

    public ConversationMemberSettingsRepositoryAdapter(SpringDataConversationMemberSettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConversationMemberSettings save(ConversationMemberSettings settings) {
        return mapper.toDomain(repository.save(mapper.toEntity(settings)));
    }

    @Override
    public Optional<ConversationMemberSettings> findByConversationIdAndUserId(UUID conversationId, Long userId) {
        return repository.findByIdConversationIdAndIdUserId(conversationId, userId).map(mapper::toDomain);
    }
}

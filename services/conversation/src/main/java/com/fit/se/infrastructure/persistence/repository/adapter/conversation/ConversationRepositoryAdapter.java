package com.fit.se.infrastructure.persistence.repository.adapter.conversation;

import com.fit.se.domain.conversation.model.Conversation;
import com.fit.se.domain.conversation.repository.ConversationRepository;
import com.fit.se.infrastructure.persistence.mapper.conversation.ConversationPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.conversation.SpringDataConversationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConversationRepositoryAdapter implements ConversationRepository {

    private final SpringDataConversationJpaRepository repository;
    private final ConversationPersistenceMapper mapper = new ConversationPersistenceMapper();

    public ConversationRepositoryAdapter(SpringDataConversationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Conversation save(Conversation conversation) {
        return mapper.toDomain(repository.save(mapper.toEntity(conversation)));
    }

    @Override
    public Optional<Conversation> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}

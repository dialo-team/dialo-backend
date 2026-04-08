package com.fit.se.infrastructure.persistence.repository.adapter.conversation;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.repository.ConversationRepository;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.conversation.valueobject.DirectPairKey;
import com.fit.se.infrastructure.persistence.mapper.conversation.ConversationPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.conversation.SpringDataConversationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public Optional<Conversation> findById(ConversationId id) {
        return Optional.empty();
    }

    @Override
    public Conversation save(Conversation conversation) {
        return mapper.toDomain(repository.save(mapper.toEntity(conversation)));
    }

    @Override
    public boolean existsByDirectPairKey(DirectPairKey directPairKey) {
        return false;
    }

    @Override
    public Optional<Conversation> findDirectConversation(DirectPairKey directPairKey) {
        return Optional.empty();
    }

    @Override
    public boolean existsSelfConversation(UserId userId) {
        return false;
    }

    @Override
    public boolean existsSystemConversation(UserId userId) {
        return false;
    }

    @Override
    public List<Conversation> findAllByUserId(UserId userId) {
        return List.of();
    }
}

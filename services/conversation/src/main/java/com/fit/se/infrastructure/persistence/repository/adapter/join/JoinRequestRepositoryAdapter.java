package com.fit.se.infrastructure.persistence.repository.adapter.join;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.join.aggregate.JoinRequest;
import com.fit.se.domain.join.aggregate.JoinRequestId;
import com.fit.se.domain.join.aggregate.JoinRequestStatus;
import com.fit.se.domain.join.repository.JoinRequestRepository;
import com.fit.se.infrastructure.persistence.mapper.join.JoinPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.join.SpringDataJoinRequestJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JoinRequestRepositoryAdapter implements JoinRequestRepository {

    private final SpringDataJoinRequestJpaRepository repository;
    private final JoinPersistenceMapper mapper = new JoinPersistenceMapper();

    public JoinRequestRepositoryAdapter(SpringDataJoinRequestJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<JoinRequest> findById(JoinRequestId id) {
        return Optional.empty();
    }

    @Override
    public List<JoinRequest> findAllByConversationIdAndStatus(ConversationId conversationId, JoinRequestStatus status) {
        return List.of();
    }

    @Override
    public Optional<JoinRequest> findPendingByConversationIdAndRequesterId(ConversationId conversationId, UserId requesterId) {
        return Optional.empty();
    }

    @Override
    public JoinRequest save(JoinRequest request) {
        return mapper.toDomain(repository.save(mapper.toEntity(request)));
    }

    @Override
    public Optional<JoinRequest> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<JoinRequest> findAllByConversationId(UUID conversationId) {
        return repository.findAllByConversationId(conversationId).stream().map(mapper::toDomain).toList();
    }
}

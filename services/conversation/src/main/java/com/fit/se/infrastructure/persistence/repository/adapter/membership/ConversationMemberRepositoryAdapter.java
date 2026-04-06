package com.fit.se.infrastructure.persistence.repository.adapter.membership;

import com.fit.se.domain.membership.model.ConversationMember;
import com.fit.se.domain.membership.repository.ConversationMemberRepository;
import com.fit.se.infrastructure.persistence.mapper.membership.MembershipPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.membership.SpringDataConversationMemberJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConversationMemberRepositoryAdapter implements ConversationMemberRepository {

    private final SpringDataConversationMemberJpaRepository repository;
    private final MembershipPersistenceMapper mapper = new MembershipPersistenceMapper();

    public ConversationMemberRepositoryAdapter(SpringDataConversationMemberJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConversationMember save(ConversationMember member) {
        return mapper.toDomain(repository.save(mapper.toEntity(member)));
    }

    @Override
    public Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, Long userId) {
        return repository.findByIdConversationIdAndIdUserId(conversationId, userId).map(mapper::toDomain);
    }

    @Override
    public List<ConversationMember> findAllByConversationId(UUID conversationId) {
        return repository.findAllByIdConversationId(conversationId).stream().map(mapper::toDomain).toList();
    }
}

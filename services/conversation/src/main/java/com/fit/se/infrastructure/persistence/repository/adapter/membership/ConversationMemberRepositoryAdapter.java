package com.fit.se.infrastructure.persistence.repository.adapter.membership;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.membership.aggregate.ConversationMember;
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

//    @Override
//    public Optional<ConversationMember> findByConversationIdAndUserId(ConversationId conversationId, UserId userId) {
//        return Optional.empty();
//    }
//
//    @Override
//    public List<ConversationMember> findActiveByConversationId(ConversationId conversationId) {
//        return List.of();
//    }
//
//    @Override
//    public Optional<ConversationMember> findActiveOwnerByConversationId(ConversationId conversationId) {
//        return Optional.empty();
//    }
//
//    @Override
//    public List<ConversationMember> findActiveMembersByConversationId(ConversationId conversationId) {
//        return List.of();
//    }
//
//    @Override
//    public ConversationMember save(ConversationMember member) {
//        return mapper.toDomain(repository.save(mapper.toEntity(member)));
//    }
//
//    @Override
//    public void saveAll(List<ConversationMember> members) {
//
//    }
//
//    @Override
//    public boolean existsActiveMembership(ConversationId conversationId, UserId userId) {
//        return false;
//    }


    @Override
    public com.fit.se.domain.membership.model.ConversationMember save(com.fit.se.domain.membership.model.ConversationMember member) {
        return null;
    }

    @Override
    public Optional<com.fit.se.domain.membership.model.ConversationMember> findByConversationIdAndUserId(UUID conversationId, Long userId) {
        return Optional.empty();
    }

    @Override
    public List<com.fit.se.domain.membership.model.ConversationMember> findAllByConversationId(UUID conversationId) {
        return List.of();
    }

    @Override
    public Optional<ConversationMember> findByConversationIdAndUserId(ConversationId conversationId, UserId userId) {
        return Optional.empty();
    }

    @Override
    public List<ConversationMember> findActiveByConversationId(ConversationId conversationId) {
        return List.of();
    }

    @Override
    public Optional<ConversationMember> findActiveOwnerByConversationId(ConversationId conversationId) {
        return Optional.empty();
    }

    @Override
    public List<ConversationMember> findActiveMembersByConversationId(ConversationId conversationId) {
        return List.of();
    }

    @Override
    public ConversationMember save(ConversationMember member) {
        return null;
    }

    @Override
    public void saveAll(List<ConversationMember> members) {

    }

    @Override
    public boolean existsActiveMembership(ConversationId conversationId, UserId userId) {
        return false;
    }
}

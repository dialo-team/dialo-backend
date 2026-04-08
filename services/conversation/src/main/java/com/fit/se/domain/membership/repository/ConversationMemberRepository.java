package com.fit.se.domain.membership.repository;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.membership.aggregate.ConversationMember;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository {
    Optional<ConversationMember> findByConversationIdAndUserId(ConversationId conversationId, UserId userId);

    List<ConversationMember> findActiveByConversationId(ConversationId conversationId);

    Optional<ConversationMember> findActiveOwnerByConversationId(ConversationId conversationId);

    List<ConversationMember> findActiveMembersByConversationId(ConversationId conversationId);

    ConversationMember save(ConversationMember member);

    void saveAll(List<ConversationMember> members);

    boolean existsActiveMembership(ConversationId conversationId, UserId userId);
}
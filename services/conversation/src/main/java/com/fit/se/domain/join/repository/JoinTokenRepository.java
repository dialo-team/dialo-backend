package com.fit.se.domain.join.repository;

import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.conversation.valueobject.JoinToken;

import java.util.Optional;

public interface JoinTokenRepository {
    Optional<JoinToken> findActiveByConversationId(ConversationId conversationId);

    Optional<ConversationId> findConversationIdByToken(JoinToken joinToken);

    void save(ConversationId conversationId, JoinToken joinToken);

    void deleteByConversationId(ConversationId conversationId);
}

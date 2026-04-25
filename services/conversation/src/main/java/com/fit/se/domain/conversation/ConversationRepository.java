package com.fit.se.domain.conversation;

import com.fit.se.domain.direct.DirectConversation;

import java.util.Optional;

public interface ConversationRepository {
    Optional<DirectConversation> findDirectByParticipants(String userId1, String userId2);
    Optional<Conversation> findById(String conversationId);

    void save(Conversation conversation);
}
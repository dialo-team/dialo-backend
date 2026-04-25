package com.fit.se.infrastructure.persistence.repository.adapter;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationRepository;
import com.fit.se.domain.direct.DirectConversation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConversationAdapter implements ConversationRepository {
    @Override
    public Optional<DirectConversation> findDirectByParticipants(String userId1, String userId2) {
        return Optional.empty();
    }

    @Override
    public Optional<Conversation> findById(String conversationId) {
        return Optional.empty();
    }

    @Override
    public void save(Conversation conversation) {

    }
}

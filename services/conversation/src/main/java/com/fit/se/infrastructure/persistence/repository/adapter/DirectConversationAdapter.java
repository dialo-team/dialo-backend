package com.fit.se.infrastructure.persistence.repository.adapter;

import com.fit.se.domain.direct.DirectConversation;
import com.fit.se.domain.direct.DirectConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DirectConversationAdapter implements DirectConversationRepository {
    @Override
    public Optional<DirectConversation> findByParticipants(String user1Id, String user2Id) {
        return Optional.empty();
    }

    @Override
    public DirectConversation save(DirectConversation conversation) {
        return null;
    }
}

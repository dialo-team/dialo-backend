package com.fit.se.domain.direct;

import java.util.Optional;

public interface DirectConversationRepository {
    Optional<DirectConversation> findByParticipants(String user1Id, String user2Id);
    DirectConversation save(DirectConversation conversation);
}
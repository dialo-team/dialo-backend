package com.fit.se.repository;

import com.fit.se.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    Optional<Conversation> findByParticipantKey(String participantKey);
    List<Conversation> findByParticipantsContainingOrderByUpdatedAtDesc(String participantId);
}

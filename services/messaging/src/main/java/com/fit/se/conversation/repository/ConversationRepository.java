package com.fit.se.conversation.repository;

import com.fit.se.conversation.domain.Conversation;
import com.fit.se.conversation.domain.ConversationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    Optional<Conversation> findByIdAndStatus(String id, ConversationStatus status);
    Optional<Conversation> findByTypeAndDirectKeyAndStatus(com.fit.se.conversation.domain.ConversationType type, String directKey, ConversationStatus status);
    List<Conversation> findByIdInAndStatusOrderByLastMessageAtDesc(List<String> ids, ConversationStatus status);
}

package com.fit.se.message.repository;

import com.fit.se.message.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByConversationIdOrderByPositionAsc(String conversationId);
    List<Message> findByConversationIdAndPositionGreaterThanOrderByPositionAsc(String conversationId, Long position);
    Optional<Message> findTopByConversationIdOrderByPositionDesc(String conversationId);
}
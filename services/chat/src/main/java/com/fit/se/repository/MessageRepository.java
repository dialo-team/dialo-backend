package com.fit.se.repository;

import com.fit.se.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    List<Message> findByConversationIdAndTypeInOrderByCreatedAtDesc(String conversationId, List<String> types);
    List<Message> findByConversationIdAndContentRegexIgnoreCaseOrderByCreatedAtDesc(String conversationId, String regex);
}

package com.fit.se.message.repository;

import com.fit.se.message.domain.MessageHidden;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageHiddenRepository extends MongoRepository<MessageHidden, String> {
    boolean existsByUserIdAndMessageId(String userId, String messageId);
    List<MessageHidden> findByUserIdAndConversationId(String userId, String conversationId);
}

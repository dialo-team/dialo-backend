package com.fit.se.message.repository;

import com.fit.se.message.domain.MessageReaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends MongoRepository<MessageReaction, String> {
    Optional<MessageReaction> findByMessageIdAndUserIdAndEmoji(String messageId, String userId, int emoji);
    List<MessageReaction> findByMessageId(String messageId);
}

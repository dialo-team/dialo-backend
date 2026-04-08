package com.fit.se.domain.conversation.repository;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.conversation.valueobject.DirectPairKey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository {
    Optional<Conversation> findById(ConversationId id);

    Conversation save(Conversation conversation);

    boolean existsByDirectPairKey(DirectPairKey directPairKey);

    Optional<Conversation> findDirectConversation(DirectPairKey directPairKey);

    boolean existsSelfConversation(UserId userId);

    boolean existsSystemConversation(UserId userId);

    List<Conversation> findAllByUserId(UserId userId);

}

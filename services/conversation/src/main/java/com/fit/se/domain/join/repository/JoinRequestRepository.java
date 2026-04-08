package com.fit.se.domain.join.repository;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.join.aggregate.JoinRequest;
import com.fit.se.domain.join.aggregate.JoinRequestId;
import com.fit.se.domain.join.aggregate.JoinRequestStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JoinRequestRepository {
    Optional<JoinRequest> findById(JoinRequestId id);

    List<JoinRequest> findAllByConversationIdAndStatus(ConversationId conversationId, JoinRequestStatus status);

    Optional<JoinRequest> findPendingByConversationIdAndRequesterId(ConversationId conversationId, UserId requesterId);

    JoinRequest save(JoinRequest joinRequest);

    Optional<JoinRequest> findById(UUID id);
    List<JoinRequest> findAllByConversationId(UUID conversationId);

}

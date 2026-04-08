package com.fit.se.domain.join.aggregate;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.join.exception.JoinRequestAlreadyProcessedException;

import java.util.Objects;

public class JoinRequest {
    private final JoinRequestId id;
    private final ConversationId conversationId;
    private final UserId requesterId;
    private final JoinMethod joinMethod;
    private JoinRequestStatus status;

    private JoinRequest(JoinRequestId id, ConversationId conversationId, UserId requesterId, JoinMethod joinMethod, JoinRequestStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId must not be null");
        this.requesterId = Objects.requireNonNull(requesterId, "requesterId must not be null");
        this.joinMethod = Objects.requireNonNull(joinMethod, "joinMethod must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static JoinRequest pending(ConversationId conversationId, UserId requesterId, JoinMethod joinMethod) {
        return new JoinRequest(JoinRequestId.newId(), conversationId, requesterId, joinMethod, JoinRequestStatus.PENDING);
    }

    public JoinRequestId getId() {
        return id;
    }

    public ConversationId getConversationId() {
        return conversationId;
    }

    public UserId getRequesterId() {
        return requesterId;
    }

    public JoinMethod getJoinMethod() {
        return joinMethod;
    }

    public JoinRequestStatus getStatus() {
        return status;
    }

    public boolean isPending() {
        return status == JoinRequestStatus.PENDING;
    }

    public void approve() {
        ensurePending();
        this.status = JoinRequestStatus.APPROVED;
    }

    public void reject() {
        ensurePending();
        this.status = JoinRequestStatus.REJECTED;
    }

    public void cancel() {
        ensurePending();
        this.status = JoinRequestStatus.CANCELED;
    }

    public void expire() {
        ensurePending();
        this.status = JoinRequestStatus.EXPIRED;
    }

    private void ensurePending() {
        if (!isPending()) {
            throw new JoinRequestAlreadyProcessedException("Join request already processed");
        }
    }
}

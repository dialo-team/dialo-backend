package com.fit.se.domain.group.events;

import com.fit.se.domain.events.DomainEvent;

public class MemberAddedEvent implements DomainEvent {
    private final String conversationId;
    private final String userId;

    public MemberAddedEvent(String conversationId, String userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    @Override
    public String eventName() {
        return "MemberAdded";
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getUserId() {
        return userId;
    }
}

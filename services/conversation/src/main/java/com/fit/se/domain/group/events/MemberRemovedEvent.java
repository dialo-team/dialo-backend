package com.fit.se.domain.group.events;

import com.fit.se.domain.events.DomainEvent;

public class MemberRemovedEvent implements DomainEvent {
    private final String conversationId;
    private final String userId;

    public MemberRemovedEvent(String conversationId, String userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    @Override
    public String eventName() {
        return "MemberRemoved";
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getUserId() {
        return userId;
    }
}

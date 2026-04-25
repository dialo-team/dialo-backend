package com.fit.se.domain.group.events;

import com.fit.se.domain.events.DomainEvent;

public class GroupDissolvedEvent implements DomainEvent {
    private final String conversationId;

    public GroupDissolvedEvent(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String eventName() {
        return "GroupDissolved";
    }
}
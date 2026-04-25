package com.fit.se.domain.group.events;

import com.fit.se.domain.events.DomainEvent;

public class LeaderTransferredEvent implements DomainEvent {
    private final String conversationId;
    private final String oldLeaderId;
    private final String newLeaderId;

    public LeaderTransferredEvent(String conversationId, String oldLeaderId, String newLeaderId) {
        this.conversationId = conversationId;
        this.oldLeaderId = oldLeaderId;
        this.newLeaderId = newLeaderId;
    }

    @Override
    public String eventName() {
        return "LeaderTransferred";
    }
}

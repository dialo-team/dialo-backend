package com.fit.se.domain.group.events;

import com.fit.se.domain.events.DomainEvent;
import com.fit.se.domain.group.GroupRole;

public class RoleAssignedEvent implements DomainEvent {
    private final String conversationId;
    private final String userId;
    private final GroupRole role;

    public RoleAssignedEvent(String conversationId, String userId, GroupRole role) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
    }

    @Override
    public String eventName() {
        return "RoleAssigned";
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getUserId() {
        return userId;
    }

    public GroupRole getRole() {
        return role;
    }
}

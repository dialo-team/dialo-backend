package com.fit.se.domain.conversation.service;

import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.aggregate.GroupInfo;
import com.fit.se.domain.conversation.aggregate.GroupPermissionPolicy;
import com.fit.se.domain.conversation.valueobject.ConversationId;

public class ConversationFactory {
    public Conversation createDirect() {
        return Conversation.newDirect(ConversationId.newId());
    }

    public Conversation createGroup(GroupInfo groupInfo, GroupPermissionPolicy policy) {
        return Conversation.newGroup(ConversationId.newId(), groupInfo, policy);
    }

    public Conversation createSelf() {
        return Conversation.newSelf(ConversationId.newId());
    }

    public Conversation createSystem() {
        return Conversation.newSystem(ConversationId.newId());
    }
}

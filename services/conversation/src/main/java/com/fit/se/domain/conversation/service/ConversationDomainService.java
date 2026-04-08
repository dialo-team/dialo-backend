package com.fit.se.domain.conversation.service;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.aggregate.GroupInfo;
import com.fit.se.domain.conversation.aggregate.GroupPermissionPolicy;

import java.util.Objects;

public class ConversationDomainService {
    private final ConversationFactory conversationFactory;
    private final ConversationUniquenessChecker uniquenessChecker;

    public ConversationDomainService(
            ConversationFactory conversationFactory,
            ConversationUniquenessChecker uniquenessChecker
    ) {
        this.conversationFactory = Objects.requireNonNull(conversationFactory, "conversationFactory must not be null");
        this.uniquenessChecker = Objects.requireNonNull(uniquenessChecker, "uniquenessChecker must not be null");
    }

    public Conversation createDirectConversation(UserId firstUserId, UserId secondUserId) {
        uniquenessChecker.ensureDirectConversationDoesNotExist(firstUserId, secondUserId);
        return conversationFactory.createDirect();
    }

    public Conversation createGroupConversation(GroupInfo groupInfo, GroupPermissionPolicy permissionPolicy) {
        return conversationFactory.createGroup(groupInfo, permissionPolicy);
    }

    public Conversation createSelfConversation(UserId userId) {
        uniquenessChecker.ensureSelfConversationDoesNotExist(userId);
        return conversationFactory.createSelf();
    }

    public Conversation createSystemConversation(UserId userId) {
        uniquenessChecker.ensureSystemConversationDoesNotExist(userId);
        return conversationFactory.createSystem();
    }
}

package com.fit.se.domain.conversation.service;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.exception.ConversationAlreadyExistsException;
import com.fit.se.domain.conversation.repository.ConversationRepository;
import com.fit.se.domain.conversation.valueobject.DirectPairKey;

import java.util.Objects;

public class ConversationUniquenessChecker {
    private final ConversationRepository conversationRepository;

    public ConversationUniquenessChecker(ConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository, "conversationRepository must not be null");
    }

    public void ensureDirectConversationDoesNotExist(UserId firstUserId, UserId secondUserId) {
        DirectPairKey pairKey = DirectPairKey.of(firstUserId, secondUserId);
        if (conversationRepository.existsByDirectPairKey(pairKey)) {
            throw new ConversationAlreadyExistsException("Direct conversation already exists");
        }
    }

    public void ensureSelfConversationDoesNotExist(UserId userId) {
        if (conversationRepository.existsSelfConversation(userId)) {
            throw new ConversationAlreadyExistsException("Self conversation already exists");
        }
    }

    public void ensureSystemConversationDoesNotExist(UserId userId) {
        if (conversationRepository.existsSystemConversation(userId)) {
            throw new ConversationAlreadyExistsException("System conversation already exists");
        }
    }
}

package com.fit.se.domain.conversation.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class ConversationAlreadyExistsException extends BusinessRuleViolationException {
    public ConversationAlreadyExistsException(String message) {
        super(message);
    }

    public ConversationAlreadyExistsException() {
        this("Conversation already exists");
    }
}


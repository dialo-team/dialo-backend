package com.fit.se.domain.conversation.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class ConversationDissolvedException extends BusinessRuleViolationException {
    public ConversationDissolvedException(String message) {
        super(message);
    }

    public ConversationDissolvedException() {
        this("Conversation is dissolved");
    }
}


package com.fit.se.domain.conversation.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class InvalidConversationTypeException extends BusinessRuleViolationException {
    public InvalidConversationTypeException(String message) {
        super(message);
    }

    public InvalidConversationTypeException() {
        this("Invalid conversation type");
    }
}
package com.fit.se.domain.conversation.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class UnsupportedConversationOperationException extends BusinessRuleViolationException {
    public UnsupportedConversationOperationException(String message) {
        super(message);
    }

    public UnsupportedConversationOperationException() {
        this("Unsupported conversation operation");
    }
}

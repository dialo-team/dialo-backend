package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class CannotLeaveConversationException extends BusinessRuleViolationException {
    public CannotLeaveConversationException(String message) {
        super(message);
    }

    public CannotLeaveConversationException() {
        this("Cannot leave conversation");
    }
}

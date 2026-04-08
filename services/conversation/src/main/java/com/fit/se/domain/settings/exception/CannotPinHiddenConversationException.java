package com.fit.se.domain.settings.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class CannotPinHiddenConversationException extends BusinessRuleViolationException {
    public CannotPinHiddenConversationException(String message) {
        super(message);
    }

    public CannotPinHiddenConversationException() {
        this("Cannot pin hidden conversation");
    }
}

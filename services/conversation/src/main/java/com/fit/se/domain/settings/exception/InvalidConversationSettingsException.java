package com.fit.se.domain.settings.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class InvalidConversationSettingsException extends BusinessRuleViolationException {
    public InvalidConversationSettingsException(String message) {
        super(message);
    }

    public InvalidConversationSettingsException() {
        this("Invalid conversation settings");
    }
}

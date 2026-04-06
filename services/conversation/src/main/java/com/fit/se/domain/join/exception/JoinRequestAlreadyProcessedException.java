package com.yourcompany.conversationservice.domain.join.exception;

import com.yourcompany.conversationservice.domain.common.exception.BusinessRuleViolationException;

public class JoinRequestAlreadyProcessedException extends BusinessRuleViolationException {
    public JoinRequestAlreadyProcessedException(String message) {
        super(message);
    }

    public JoinRequestAlreadyProcessedException() {
        this("Join request already processed");
    }
}

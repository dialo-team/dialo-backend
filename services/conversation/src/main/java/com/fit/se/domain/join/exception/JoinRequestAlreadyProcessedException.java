package com.fit.se.domain.join.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class JoinRequestAlreadyProcessedException extends BusinessRuleViolationException {
    public JoinRequestAlreadyProcessedException(String message) {
        super(message);
    }

    public JoinRequestAlreadyProcessedException() {
        this("Join request already processed");
    }
}

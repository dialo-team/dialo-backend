package com.fit.se.domain.join.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class JoinNotAllowedException extends BusinessRuleViolationException {
    public JoinNotAllowedException(String message) {
        super(message);
    }

    public JoinNotAllowedException() {
        this("Join not allowed");
    }
}

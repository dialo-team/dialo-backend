package com.fit.se.domain.join.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class JoinTokenExpiredException extends BusinessRuleViolationException {
    public JoinTokenExpiredException(String message) {
        super(message);
    }

    public JoinTokenExpiredException() {
        this("Join token expired");
    }
}

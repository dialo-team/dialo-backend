package com.fit.se.domain.join.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class JoinTokenInvalidException extends BusinessRuleViolationException {
    public JoinTokenInvalidException(String message) {
        super(message);
    }

    public JoinTokenInvalidException() {
        this("Invalid join token");
    }
}

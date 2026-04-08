package com.fit.se.domain.bootstrap.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class UserBootstrapFailedException extends BusinessRuleViolationException {
    public UserBootstrapFailedException(String message) {
        super(message);
    }

    public UserBootstrapFailedException() {
        this("User bootstrap failed");
    }
}

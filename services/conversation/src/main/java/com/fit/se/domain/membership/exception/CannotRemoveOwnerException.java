package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class CannotRemoveOwnerException extends BusinessRuleViolationException {
    public CannotRemoveOwnerException(String message) {
        super(message);
    }

    public CannotRemoveOwnerException() {
        this("Cannot remove owner");
    }
}


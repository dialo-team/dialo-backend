package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class OwnerRequiredException extends BusinessRuleViolationException {
    public OwnerRequiredException(String message) {
        super(message);
    }

    public OwnerRequiredException() {
        this("Owner is required");
    }
}

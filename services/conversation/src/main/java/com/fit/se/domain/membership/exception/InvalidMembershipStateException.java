package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class InvalidMembershipStateException extends BusinessRuleViolationException {
    public InvalidMembershipStateException(String message) {
        super(message);
    }

    public InvalidMembershipStateException() {
        this("Invalid membership state");
    }
}

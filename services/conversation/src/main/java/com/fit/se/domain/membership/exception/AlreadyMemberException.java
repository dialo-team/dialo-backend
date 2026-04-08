package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class AlreadyMemberException extends BusinessRuleViolationException {
    public AlreadyMemberException(String message) {
        super(message);
    }

    public AlreadyMemberException() {
        this("Already a member");
    }
}

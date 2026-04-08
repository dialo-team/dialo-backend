package com.fit.se.domain.join.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class JoinApprovalRequiredException extends BusinessRuleViolationException {
    public JoinApprovalRequiredException(String message) {
        super(message);
    }

    public JoinApprovalRequiredException() {
        this("Join approval required");
    }
}

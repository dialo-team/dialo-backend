package com.fit.se.domain.settings.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class LabelAssignmentNotAllowedException extends BusinessRuleViolationException {
    public LabelAssignmentNotAllowedException(String message) {
        super(message);
    }

    public LabelAssignmentNotAllowedException() {
        this("Label assignment is not allowed");
    }
}

package com.fit.se.domain.label.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class LabelDeletionNotAllowedException extends BusinessRuleViolationException {
    public LabelDeletionNotAllowedException(String message) {
        super(message);
    }

    public LabelDeletionNotAllowedException() {
        this("Label deletion is not allowed");
    }
}

package com.fit.se.domain.label.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class LabelRenameNotAllowedException extends BusinessRuleViolationException {
    public LabelRenameNotAllowedException(String message) {
        super(message);
    }

    public LabelRenameNotAllowedException() {
        this("Label rename is not allowed");
    }
}

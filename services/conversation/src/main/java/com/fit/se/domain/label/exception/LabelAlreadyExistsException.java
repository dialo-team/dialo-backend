package com.fit.se.domain.label.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class LabelAlreadyExistsException extends BusinessRuleViolationException {
    public LabelAlreadyExistsException(String message) {
        super(message);
    }

    public LabelAlreadyExistsException() {
        this("Label already exists");
    }
}

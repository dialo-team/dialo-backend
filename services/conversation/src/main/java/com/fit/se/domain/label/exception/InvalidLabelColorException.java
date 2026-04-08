package com.fit.se.domain.label.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class InvalidLabelColorException extends BusinessRuleViolationException {
    public InvalidLabelColorException(String message) {
        super(message);
    }

    public InvalidLabelColorException() {
        this("Invalid label color");
    }
}

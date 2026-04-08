package com.fit.se.domain.settings.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class HideNotAllowedException extends BusinessRuleViolationException {
    public HideNotAllowedException(String message) {
        super(message);
    }

    public HideNotAllowedException() {
        this("Hide is not allowed");
    }
}

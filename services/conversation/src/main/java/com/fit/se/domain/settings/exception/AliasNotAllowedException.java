package com.fit.se.domain.settings.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class AliasNotAllowedException extends BusinessRuleViolationException {
    public AliasNotAllowedException(String message) {
        super(message);
    }

    public AliasNotAllowedException() {
        this("Alias is not allowed");
    }
}

package com.fit.se.domain.label.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class LabelOwnershipException extends BusinessRuleViolationException {
    public LabelOwnershipException(String message) {
        super(message);
    }

    public LabelOwnershipException() {
        this("Label does not belong to user");
    }
}

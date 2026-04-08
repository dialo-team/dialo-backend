package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class OwnershipTransferRequiredException extends BusinessRuleViolationException {
    public OwnershipTransferRequiredException(String message) {
        super(message);
    }

    public OwnershipTransferRequiredException() {
        this("Ownership transfer required");
    }
}

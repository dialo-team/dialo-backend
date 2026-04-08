package com.fit.se.domain.block.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class BlockOperationNotAllowedException extends BusinessRuleViolationException {
    public BlockOperationNotAllowedException(String message) {
        super(message);
    }

    public BlockOperationNotAllowedException() {
        this("Block operation not allowed");
    }
}

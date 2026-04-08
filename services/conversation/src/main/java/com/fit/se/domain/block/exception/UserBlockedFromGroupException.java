package com.fit.se.domain.block.exception;


import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class UserBlockedFromGroupException extends BusinessRuleViolationException {
    public UserBlockedFromGroupException(String message) {
        super(message);
    }

    public UserBlockedFromGroupException() {
        this("User is blocked from group");
    }
}

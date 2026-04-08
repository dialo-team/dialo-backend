package com.fit.se.domain.settings.exception;

import com.fit.se.domain.common.exception.BusinessRuleViolationException;

public class MuteNotAllowedException extends BusinessRuleViolationException {
    public MuteNotAllowedException(String message) {
        super(message);
    }

    public MuteNotAllowedException() {
        this("Mute is not allowed");
    }
}

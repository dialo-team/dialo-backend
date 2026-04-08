package com.fit.se.domain.join.exception;

import com.fit.se.domain.common.exception.ResourceNotFoundException;

public class JoinRequestNotFoundException extends ResourceNotFoundException {
    public JoinRequestNotFoundException(String message) {
        super(message);
    }

    public JoinRequestNotFoundException() {
        this("Join request not found");
    }
}

package com.fit.se.domain.membership.exception;

import com.fit.se.domain.common.exception.ResourceNotFoundException;

public class MemberNotFoundException extends ResourceNotFoundException {
    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException() {
        this("Member not found");
    }
}


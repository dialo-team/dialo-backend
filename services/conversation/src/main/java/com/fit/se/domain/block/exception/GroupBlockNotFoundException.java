package com.fit.se.domain.block.exception;

import com.fit.se.domain.common.exception.ResourceNotFoundException;

public class GroupBlockNotFoundException extends ResourceNotFoundException {
    public GroupBlockNotFoundException(String message) {
        super(message);
    }

    public GroupBlockNotFoundException() {
        this("Group block not found");
    }
}

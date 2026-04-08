package com.fit.se.domain.label.exception;

import com.fit.se.domain.common.exception.ResourceNotFoundException;

public class LabelNotFoundException extends ResourceNotFoundException {
    public LabelNotFoundException(String message) {
        super(message);
    }

    public LabelNotFoundException() {
        this("Label not found");
    }
}

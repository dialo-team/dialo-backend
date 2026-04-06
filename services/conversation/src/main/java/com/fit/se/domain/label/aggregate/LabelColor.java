package com.fit.se.domain.label.valueobject;

import com.fit.se.domain.label.exception.InvalidLabelColorException;

import java.util.Objects;

public record LabelColor(String value) {
    public LabelColor {
        Objects.requireNonNull(value, "label color must not be null");
        if (!value.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new InvalidLabelColorException("Label color must be in hex format like #A1B2C3");
        }
    }
}

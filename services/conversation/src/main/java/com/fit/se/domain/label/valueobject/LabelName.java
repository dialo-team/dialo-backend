package com.fit.se.domain.label.valueobject;

import java.util.Objects;

public record LabelName(String value) {
    public LabelName {
        Objects.requireNonNull(value, "label name must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("label name must not be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("label name must be <= 50 characters");
        }
    }

    public boolean equalsIgnoreCase(LabelName other) {
        return other != null && value.equalsIgnoreCase(other.value);
    }
}

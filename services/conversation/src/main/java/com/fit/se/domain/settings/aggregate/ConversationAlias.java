package com.fit.se.domain.settings.aggregate;

import java.util.Objects;

public record ConversationAlias(String value) {
    public ConversationAlias {
        Objects.requireNonNull(value, "alias must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("alias must not be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("alias must be <= 100 characters");
        }
    }
}

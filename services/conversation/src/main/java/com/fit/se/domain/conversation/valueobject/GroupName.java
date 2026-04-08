package com.fit.se.domain.conversation.valueobject;

import java.util.Objects;

public record GroupName(String value) {
    public GroupName {
        Objects.requireNonNull(value, "groupName must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("groupName must not be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("groupName must be <= 100 characters");
        }
    }
}

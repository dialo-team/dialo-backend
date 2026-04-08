package com.fit.se.domain.block.aggregate;

import java.util.Objects;

public record BlockReason(String value) {
    public BlockReason {
        Objects.requireNonNull(value, "block reason must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("block reason must not be blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("block reason must be <= 255 characters");
        }
    }
}

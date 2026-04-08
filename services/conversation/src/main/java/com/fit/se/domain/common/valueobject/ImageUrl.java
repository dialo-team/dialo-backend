package com.fit.se.domain.common.valueobject;

import java.net.URI;
import java.util.Objects;

public record ImageUrl(String value) {
    public ImageUrl {
        Objects.requireNonNull(value, "imageUrl must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("imageUrl must not be blank");
        }
        URI.create(value);
    }
}
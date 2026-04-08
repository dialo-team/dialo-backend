package com.fit.se.domain.label.valueobject;

import java.util.Objects;
import java.util.UUID;

public record LabelId(UUID value) {
    public LabelId {
        Objects.requireNonNull(value, "labelId must not be null");
    }

    public static LabelId newId() {
        return new LabelId(UUID.randomUUID());
    }
}

package com.fit.se.domain.join.aggregate;

import java.util.Objects;
import java.util.UUID;

public record JoinRequestId(UUID value) {
    public JoinRequestId {
        Objects.requireNonNull(value, "joinRequestId must not be null");
    }

    public static JoinRequestId newId() {
        return new JoinRequestId(UUID.randomUUID());
    }
}

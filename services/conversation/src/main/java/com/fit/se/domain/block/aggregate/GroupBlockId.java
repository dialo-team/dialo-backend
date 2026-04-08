package com.fit.se.domain.block.aggregate;

import java.util.Objects;
import java.util.UUID;

public record GroupBlockId(UUID value) {
    public GroupBlockId {
        Objects.requireNonNull(value, "groupBlockId must not be null");
    }

    public static GroupBlockId newId() {
        return new GroupBlockId(UUID.randomUUID());
    }
}

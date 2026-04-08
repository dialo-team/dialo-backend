package com.fit.se.domain.common.aggregate;

import java.time.Instant;
import java.util.Objects;

public abstract class BaseEntity<ID> {
    private final ID id;
    private final Instant createdAt;
    private Instant updatedAt;

    protected BaseEntity(ID id) {
        this(id, Instant.now(), Instant.now());
    }

    protected BaseEntity(ID id, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public ID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    protected void touch() {
        this.updatedAt = Instant.now();
    }
}

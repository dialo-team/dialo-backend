package com.fit.se.domain.common.valueobject;

import java.time.Instant;
import java.util.Objects;

public record AuditInfo(UserId createdBy, Instant createdAt, UserId updatedBy, Instant updatedAt) {
    public AuditInfo {
        Objects.requireNonNull(createdBy, "createdBy must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedBy, "updatedBy must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}

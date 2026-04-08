package com.fit.se.domain.membership.valueobject;

import java.util.Objects;
import java.util.UUID;

public record MembershipId(UUID value) {
    public MembershipId {
        Objects.requireNonNull(value, "membershipId must not be null");
    }

    public static MembershipId newId() {
        return new MembershipId(UUID.randomUUID());
    }
}


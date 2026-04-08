package com.fit.se.domain.conversation.valueobject;

import com.fit.se.domain.common.valueobject.UserId;

import java.util.Objects;

public record DirectPairKey(Long leftUserId, Long rightUserId) {
    public DirectPairKey {
        Objects.requireNonNull(leftUserId, "leftUserId must not be null");
        Objects.requireNonNull(rightUserId, "rightUserId must not be null");
        if (leftUserId <= 0 || rightUserId <= 0) {
            throw new IllegalArgumentException("user ids must be positive");
        }
        if (leftUserId.equals(rightUserId)) {
            throw new IllegalArgumentException("direct conversation requires two different users");
        }
        if (leftUserId > rightUserId) {
            throw new IllegalArgumentException("leftUserId must be <= rightUserId");
        }
    }

    public static DirectPairKey of(UserId a, UserId b) {
        long first = a.value();
        long second = b.value();
        return first <= second ? new DirectPairKey(first, second) : new DirectPairKey(second, first);
    }
}

package com.fit.se.user.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class RelationPrivacyOverride {
    private final String targetUserId;
    private final RelationPrivacyKey key;
    private final RelationPrivacyDecision decision;

    public RelationPrivacyOverride(
            String targetUserId,
            RelationPrivacyKey key,
            RelationPrivacyDecision decision
    ) {
        if (targetUserId == null || targetUserId.isBlank()) {
            throw new IllegalArgumentException("Target user id must not be blank");
        }
        if (key == null) {
            throw new IllegalArgumentException("Relation privacy key must not be null");
        }
        if (decision == null) {
            throw new IllegalArgumentException("Relation privacy decision must not be null");
        }

        this.targetUserId = targetUserId;
        this.key = key;
        this.decision = decision;
    }

    public boolean matches(String userId, RelationPrivacyKey key) {
        return Objects.equals(this.targetUserId, userId) && this.key == key;
    }
}
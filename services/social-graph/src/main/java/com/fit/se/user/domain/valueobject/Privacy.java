package com.fit.se.user.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@EqualsAndHashCode
@Getter
public class Privacy {
    private final BirthdayPrivacy birthdayPrivacy;
    private final Set<RelationPrivacyOverride> relationOverrides;

    public Privacy(
            BirthdayPrivacy birthdayPrivacy,
            Set<RelationPrivacyOverride> relationOverrides
    ) {
        if (birthdayPrivacy == null) {
            throw new IllegalArgumentException("Birthday privacy must not be null");
        }

        this.birthdayPrivacy = birthdayPrivacy;
        this.relationOverrides = relationOverrides == null
                ? new HashSet<>()
                : new HashSet<>(relationOverrides);
    }

    public static Privacy defaultPrivacy() {
        return new Privacy(
                new BirthdayPrivacy(BirthdayVisibility.HIDDEN, false),
                Set.of()
        );
    }

    public BirthdayPrivacy birthdayPrivacy() {
        return birthdayPrivacy;
    }

    public Set<RelationPrivacyOverride> relationOverrides() {
        return Set.copyOf(relationOverrides);
    }

    public Optional<RelationPrivacyDecision> findDecisionFor(
            String targetUserId,
            RelationPrivacyKey key
    ) {
        return relationOverrides.stream()
                .filter(it -> it.matches(targetUserId, key))
                .map(RelationPrivacyOverride::getDecision)
                .findFirst();
    }
}
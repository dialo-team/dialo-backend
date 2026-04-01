package com.fit.se.domain.user.valueobject;

import lombok.Getter;

@Getter
public class BirthdayPrivacy {
    private final BirthdayVisibility visibility;
    private final boolean notifyFriends;

    public BirthdayPrivacy(BirthdayVisibility visibility, boolean notifyFriends) {
        if (visibility == null) {
            throw new IllegalArgumentException("Birthday visibility must not be null");
        }
        if (visibility == BirthdayVisibility.HIDDEN && notifyFriends) {
            throw new IllegalArgumentException(
                    "Cannot enable birthday notification when birthday is hidden"
            );
        }

        this.visibility = visibility;
        this.notifyFriends = notifyFriends;
    }
}
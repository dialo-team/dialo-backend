package com.fit.se.domain.policy;

import com.fit.se.domain.user.aggregate.User;
import com.fit.se.domain.user.valueobject.BirthdayVisibility;
import org.springframework.stereotype.Component;

@Component
public class BirthdayVisibilityPolicy {

    public BirthdayVisibility resolveVisibility(
            User viewer,
            User owner,
            RelationContext relationContext
    ) {
        requireUser(viewer, "viewer");
        requireUser(owner, "owner");

        if (viewer.getId().equals(owner.getId())) {
            return BirthdayVisibility.FULL_DATE;
        }

        if (relationContext != null && relationContext.isBlockedBetween(viewer.getId(), owner.getId())) {
            return BirthdayVisibility.HIDDEN;
        }

        return owner.getPrivacy()
                .birthdayPrivacy()
                .getVisibility();
    }

    public boolean canNotifyFriendsBirthday(User owner) {
        requireUser(owner, "owner");
        return owner.getPrivacy()
                .birthdayPrivacy()
                .isNotifyFriends();
    }

    private void requireUser(User user, String name) {
        if (user == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }
}
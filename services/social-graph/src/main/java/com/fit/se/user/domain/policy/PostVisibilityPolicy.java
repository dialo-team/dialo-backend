package com.fit.se.user.domain.policy;

import com.fit.se.user.domain.aggregate.User;
import org.springframework.stereotype.Component;

@Component
public class PostVisibilityPolicy {

    public boolean canViewPost(
            User viewer,
            User owner,
            RelationContext relationContext
    ) {
        requireUser(viewer, "viewer");
        requireUser(owner, "owner");

        if (viewer.getId().equals(owner.getId())) {
            return true;
        }

        if (!relationContext.areFriends()) {
            return false;
        }

        if (relationContext != null && relationContext.isBlockedBetween(viewer.getId(), owner.getId())) {
            return false;
        }

        return true;
    }

    private void requireUser(User user, String name) {
        if (user == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }
}
package com.fit.se.user.domain.policy;

import com.fit.se.user.domain.aggregate.User;
import com.fit.se.user.domain.valueobject.RelationPrivacyDecision;
import com.fit.se.user.domain.valueobject.RelationPrivacyKey;
import org.springframework.stereotype.Component;

@Component
public class ActivityVisibilityPolicy {

    public boolean canViewActivity(
            User viewer,
            User owner,
            RelationContext relationContext
    ) {
        requireUser(viewer, "viewer");
        requireUser(owner, "owner");

        if (viewer.getId().equals(owner.getId())) {
            return true;
        }

        if (relationContext != null && relationContext.isBlockedBetween(viewer.getId(), owner.getId())) {
            return false;
        }

        var ownerDecision = owner.getPrivacy()
                .findDecisionFor(viewer.getId(), RelationPrivacyKey.VIEW_MY_ACTIVITY);

        if (ownerDecision.isPresent() && ownerDecision.get() == RelationPrivacyDecision.DENY) {
            return false;
        }

        var viewerDecision = viewer.getPrivacy()
                .findDecisionFor(owner.getId(), RelationPrivacyKey.VIEW_THEIR_ACTIVITY);

        if (viewerDecision.isPresent() && viewerDecision.get() == RelationPrivacyDecision.DENY) {
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
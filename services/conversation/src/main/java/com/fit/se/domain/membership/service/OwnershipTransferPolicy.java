package com.fit.se.domain.membership.service;

import com.fit.se.domain.membership.aggregate.ConversationMember;
import com.fit.se.domain.membership.exception.MemberNotFoundException;
import com.fit.se.domain.membership.exception.OwnershipTransferRequiredException;

import java.util.List;

public class OwnershipTransferPolicy {
    public void ensureTransferTargetExists(List<ConversationMember> activeMembers, Long targetUserId) {
        boolean exists = activeMembers.stream()
                .anyMatch(member -> member.getUserId().value().equals(targetUserId) && member.isActive());
        if (!exists) {
            throw new MemberNotFoundException("Transfer target is not an active member");
        }
    }

    public void ensureTransferRequiredWhenOwnerLeaves(boolean ownerIsOnlyMember, Long targetUserId) {
        if (!ownerIsOnlyMember && targetUserId == null) {
            throw new OwnershipTransferRequiredException("Ownership transfer target is required");
        }
    }
}

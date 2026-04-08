package com.fit.se.domain.membership.service;

import com.fit.se.domain.membership.aggregate.ConversationMember;
import com.fit.se.domain.membership.exception.AlreadyMemberException;
import com.fit.se.domain.membership.exception.OwnerRequiredException;

import java.util.List;

public class MembershipPolicy {
    public void ensureNotAlreadyActiveMember(ConversationMember member) {
        if (member != null && member.isActive()) {
            throw new AlreadyMemberException("User is already an active member");
        }
    }

    public void ensureExactlyOneOwner(List<ConversationMember> activeMembers) {
        long ownerCount = activeMembers.stream().filter(ConversationMember::isOwner).count();
        if (ownerCount != 1) {
            throw new OwnerRequiredException("Group must contain exactly one active owner");
        }
    }
}

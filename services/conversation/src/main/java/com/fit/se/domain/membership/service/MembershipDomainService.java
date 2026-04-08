package com.fit.se.domain.membership.service;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.membership.aggregate.ConversationMember;
import com.fit.se.domain.membership.exception.MemberNotFoundException;

import java.util.List;
import java.util.Objects;

public class MembershipDomainService {
    private final MembershipPolicy membershipPolicy;
    private final OwnershipTransferPolicy ownershipTransferPolicy;

    public MembershipDomainService(MembershipPolicy membershipPolicy, OwnershipTransferPolicy ownershipTransferPolicy) {
        this.membershipPolicy = Objects.requireNonNull(membershipPolicy, "membershipPolicy must not be null");
        this.ownershipTransferPolicy = Objects.requireNonNull(ownershipTransferPolicy, "ownershipTransferPolicy must not be null");
    }

    public ConversationMember createOwner(ConversationId conversationId, UserId ownerId) {
        return ConversationMember.owner(conversationId, ownerId);
    }

    public ConversationMember createMember(ConversationId conversationId, UserId userId) {
        return ConversationMember.member(conversationId, userId);
    }

    public void transferOwnership(List<ConversationMember> activeMembers, UserId currentOwnerId, UserId newOwnerId) {
        ownershipTransferPolicy.ensureTransferTargetExists(activeMembers, newOwnerId.value());

        ConversationMember currentOwner = activeMembers.stream()
                .filter(member -> member.getUserId().equals(currentOwnerId) && member.isOwner())
                .findFirst()
                .orElseThrow(() -> new MemberNotFoundException("Current owner not found"));

        ConversationMember newOwner = activeMembers.stream()
                .filter(member -> member.getUserId().equals(newOwnerId))
                .findFirst()
                .orElseThrow(() -> new MemberNotFoundException("New owner not found"));

        currentOwner.assignMember();
        newOwner.assignOwner();
        membershipPolicy.ensureExactlyOneOwner(activeMembers);
    }
}
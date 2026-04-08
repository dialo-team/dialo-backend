package com.fit.se.domain.join.service;

import com.fit.se.domain.block.aggregate.GroupBlock;
import com.fit.se.domain.block.exception.UserBlockedFromGroupException;
import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.exception.ConversationDissolvedException;
import com.fit.se.domain.join.aggregate.ApprovalMode;
import com.fit.se.domain.join.aggregate.JoinMethod;
import com.fit.se.domain.join.exception.JoinApprovalRequiredException;
import com.fit.se.domain.join.exception.JoinNotAllowedException;
import com.fit.se.domain.membership.aggregate.ConversationMember;

public class JoinPolicyService {
    public void ensureCanAttemptJoin(
            Conversation conversation,
            ConversationMember existingMember,
            GroupBlock block
    ) {
        if (!conversation.isActive()) {
            throw new ConversationDissolvedException("Trưởng nhóm đã giải tán nhóm");
        }
        if (!conversation.isGroup()) {
            throw new JoinNotAllowedException("Join is allowed only for group conversations");
        }
        if (existingMember != null && existingMember.isActive()) {
            throw new JoinNotAllowedException("User is already a member");
        }
        if (block != null) {
            throw new UserBlockedFromGroupException("Trưởng nhóm đã chặn bạn khỏi nhóm");
        }
    }

    public boolean requiresApproval(Conversation conversation) {
        return conversation.getApprovalMode().orElse(ApprovalMode.AUTO_APPROVE) == ApprovalMode.OWNER_APPROVAL_REQUIRED;
    }

    public void ensureApprovalSatisfied(Conversation conversation, JoinMethod method, boolean alreadyApproved) {
        if (requiresApproval(conversation) && !alreadyApproved) {
            throw new JoinApprovalRequiredException("Owner approval is required for join method: " + method);
        }
    }

    public void ensureOwnerCanReview(UserId actorId, ConversationMember ownerMember) {
        if (ownerMember == null || !ownerMember.isOwner() || !ownerMember.getUserId().equals(actorId)) {
            throw new JoinNotAllowedException("Only owner can review join requests");
        }
    }
}

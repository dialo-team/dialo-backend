package com.fit.se.domain.block.service;

import com.fit.se.domain.block.aggregate.GroupBlock;
import com.fit.se.domain.block.exception.BlockOperationNotAllowedException;
import com.fit.se.domain.block.exception.UserBlockedFromGroupException;
import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.membership.aggregate.ConversationMember;

public class GroupBlockPolicy {
    public void ensureOwnerCanBlock(ConversationMember actor) {
        if (actor == null || !actor.isOwner()) {
            throw new BlockOperationNotAllowedException("Only owner can manage group blocks");
        }
    }

    public void ensureNotBlockingOwner(ConversationMember targetMember) {
        if (targetMember != null && targetMember.isOwner()) {
            throw new BlockOperationNotAllowedException("Owner cannot be blocked");
        }
    }

    public void ensureNotBlocked(GroupBlock groupBlock) {
        if (groupBlock != null) {
            throw new UserBlockedFromGroupException("Trưởng nhóm đã chặn bạn khỏi nhóm");
        }
    }

    public void ensureActorIsTargetOwner(UserId actorId, ConversationMember owner) {
        if (owner == null || !owner.isOwner() || !owner.getUserId().equals(actorId)) {
            throw new BlockOperationNotAllowedException("Only owner can perform this block operation");
        }
    }
}

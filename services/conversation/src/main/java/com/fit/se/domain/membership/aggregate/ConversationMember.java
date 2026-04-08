package com.fit.se.domain.membership.aggregate;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.membership.exception.CannotLeaveConversationException;
import com.fit.se.domain.membership.exception.CannotRemoveOwnerException;
import com.fit.se.domain.membership.exception.InvalidMembershipStateException;
import com.fit.se.domain.membership.valueobject.MemberRole;
import com.fit.se.domain.membership.valueobject.MembershipId;
import com.fit.se.domain.membership.valueobject.MembershipStatus;

import java.util.Objects;

public class ConversationMember {
    private final MembershipId id;
    private final ConversationId conversationId;
    private final UserId userId;
    private MemberRole role;
    private MembershipStatus status;

    private ConversationMember(MembershipId id, ConversationId conversationId, UserId userId, MemberRole role, MembershipStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static ConversationMember owner(ConversationId conversationId, UserId userId) {
        return new ConversationMember(MembershipId.newId(), conversationId, userId, MemberRole.OWNER, MembershipStatus.ACTIVE);
    }

    public static ConversationMember member(ConversationId conversationId, UserId userId) {
        return new ConversationMember(MembershipId.newId(), conversationId, userId, MemberRole.MEMBER, MembershipStatus.ACTIVE);
    }

    public MembershipId getId() {
        return id;
    }

    public ConversationId getConversationId() {
        return conversationId;
    }

    public UserId getUserId() {
        return userId;
    }

    public MemberRole getRole() {
        return role;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public boolean isOwner() {
        return role == MemberRole.OWNER && status == MembershipStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }

    public void leave() {
        ensureActive();
        if (role == MemberRole.OWNER) {
            throw new CannotLeaveConversationException("Owner cannot leave without ownership transfer or group dissolution");
        }
        this.status = MembershipStatus.LEFT;
    }

    public void forceLeaveAsTransferredOwner() {
        ensureActive();
        this.status = MembershipStatus.LEFT;
        this.role = MemberRole.MEMBER;
    }

    public void remove() {
        ensureActive();
        if (role == MemberRole.OWNER) {
            throw new CannotRemoveOwnerException("Owner cannot be removed");
        }
        this.status = MembershipStatus.REMOVED;
    }

    public void assignOwner() {
        ensureActive();
        this.role = MemberRole.OWNER;
    }

    public void assignMember() {
        ensureActive();
        this.role = MemberRole.MEMBER;
    }

    public void reactivateAsMember() {
        this.status = MembershipStatus.ACTIVE;
        this.role = MemberRole.MEMBER;
    }

    private void ensureActive() {
        if (!isActive()) {
            throw new InvalidMembershipStateException("Membership is not active");
        }
    }
}


package com.fit.se.domain.conversation.aggregate;

import java.util.Objects;

public final class GroupPermissionPolicy {
    private final MemberPermissionScope editGroupInfoScope;
    private final MemberPermissionScope sendMessageScope;
    private final MemberPermissionScope inviteMemberScope;

    public GroupPermissionPolicy(
            MemberPermissionScope editGroupInfoScope,
            MemberPermissionScope sendMessageScope,
            MemberPermissionScope inviteMemberScope
    ) {
        this.editGroupInfoScope = Objects.requireNonNull(editGroupInfoScope, "editGroupInfoScope must not be null");
        this.sendMessageScope = Objects.requireNonNull(sendMessageScope, "sendMessageScope must not be null");
        this.inviteMemberScope = Objects.requireNonNull(inviteMemberScope, "inviteMemberScope must not be null");
    }

    public static GroupPermissionPolicy defaultPolicy() {
        return new GroupPermissionPolicy(
                MemberPermissionScope.OWNER_ONLY,
                MemberPermissionScope.ALL_MEMBERS,
                MemberPermissionScope.ALL_MEMBERS
        );
    }

    public MemberPermissionScope getEditGroupInfoScope() {
        return editGroupInfoScope;
    }

    public MemberPermissionScope getSendMessageScope() {
        return sendMessageScope;
    }

    public MemberPermissionScope getInviteMemberScope() {
        return inviteMemberScope;
    }

    public boolean canEditGroupInfo(boolean isOwner) {
        return editGroupInfoScope.allows(isOwner);
    }

    public boolean canSendMessage(boolean isOwner) {
        return sendMessageScope.allows(isOwner);
    }

    public boolean canInviteMember(boolean isOwner) {
        return inviteMemberScope.allows(isOwner);
    }
}


package com.fit.se.domain.conversation.aggregate;

public enum MemberPermissionScope {
    ALL_MEMBERS,
    OWNER_ONLY;

    public boolean allows(boolean isOwner) {
        return this == ALL_MEMBERS || isOwner;
    }
}

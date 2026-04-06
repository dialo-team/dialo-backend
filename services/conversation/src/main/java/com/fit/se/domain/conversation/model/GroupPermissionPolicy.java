package com.fit.se.domain.conversation.model;

public record GroupPermissionPolicy(
        MemberPermissionScope editInfoScope,
        MemberPermissionScope sendMessageScope,
        MemberPermissionScope inviteScope
) {
}

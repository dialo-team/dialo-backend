package com.fit.se.api.dto.response.conversation;

public record PermissionPolicyResponse(
        String editInfoScope,
        String sendMessageScope,
        String inviteScope
) {
}

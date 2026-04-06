package com.fit.se.api.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;

public record UpdateGroupPermissionPolicyRequest(
        @NotBlank String editInfoScope,
        @NotBlank String sendMessageScope,
        @NotBlank String inviteScope
) {
}

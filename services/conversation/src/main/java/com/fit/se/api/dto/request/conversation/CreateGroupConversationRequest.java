package com.fit.se.api.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateGroupConversationRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1024) String avatarUrl,
        Boolean approvalRequired,
        String editInfoScope,
        String sendMessageScope,
        String inviteScope,
        List<Long> initialMemberIds
) {
}

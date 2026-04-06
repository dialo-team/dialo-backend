package com.fit.se.api.dto.response.membership;

public record MembershipActionResponse(
        String conversationId,
        Long userId,
        String action,
        String status
) {
}

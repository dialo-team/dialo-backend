package com.fit.se.api.dto.response.join;

public record JoinRequestResponse(
        String joinRequestId,
        String conversationId,
        Long requesterId,
        String joinMethod,
        String status
) {
}

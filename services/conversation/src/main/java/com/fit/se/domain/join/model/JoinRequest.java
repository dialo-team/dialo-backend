package com.fit.se.domain.join.model;

import java.util.UUID;

public record JoinRequest(
        UUID id,
        UUID conversationId,
        Long requesterId,
        JoinMethod method,
        JoinRequestStatus status
) {
}

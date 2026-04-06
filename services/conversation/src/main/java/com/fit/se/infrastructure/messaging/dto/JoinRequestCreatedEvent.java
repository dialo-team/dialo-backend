package com.fit.se.infrastructure.messaging.dto;

import java.util.UUID;

public record JoinRequestCreatedEvent(
        UUID joinRequestId,
        UUID conversationId
) {
}

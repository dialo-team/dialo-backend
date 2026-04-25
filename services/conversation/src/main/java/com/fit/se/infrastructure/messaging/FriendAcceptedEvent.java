package com.fit.se.infrastructure.messaging;

import java.time.Instant;

public record FriendAcceptedEvent(
        String eventId,
        String eventType,
        Instant occurredAt,
        String sourceService,
        FriendAcceptedPayload payload
) {
}
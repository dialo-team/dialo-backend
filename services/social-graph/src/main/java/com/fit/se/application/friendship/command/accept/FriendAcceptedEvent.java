package com.fit.se.application.friendship.command.accept;

import lombok.Builder;
import java.time.Instant;

@Builder
public record FriendAcceptedEvent(
        String eventId,
        String eventType,
        Instant occurredAt,
        String sourceService,
        FriendAcceptedPayload payload
) {
}

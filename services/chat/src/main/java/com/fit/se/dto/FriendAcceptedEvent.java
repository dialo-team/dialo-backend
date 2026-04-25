package com.fit.se.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class FriendAcceptedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String sourceService;
    private FriendAcceptedPayload payload;
}

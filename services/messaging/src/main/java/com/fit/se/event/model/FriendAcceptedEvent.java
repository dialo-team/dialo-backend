package com.fit.se.event.model;

import lombok.Data;

@Data
public class FriendAcceptedEvent {
    private String eventId;
    private String eventType;
    private FriendAcceptedPayload payload;
}
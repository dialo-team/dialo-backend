package com.fit.se.infrastructure.messaging.consumer;

public record UserCreatedEvent(
        String userId,
        String phone
) {}

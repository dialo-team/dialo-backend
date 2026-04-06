package com.fit.se.infrastructure.messaging.dto;

public record UserCreatedEventMessage(
        String userId,
        String phone
) {
}

package com.fit.se.event.model;

public record UserProfileProvisionedEvent(
        String userId,
        String phone
) {
}
package com.fit.se.event.model;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
        String userId,
        String phone
) {}


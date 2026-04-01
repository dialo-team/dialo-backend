package com.fit.se.application.user.event;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
        String userId,
        String phone
) {}

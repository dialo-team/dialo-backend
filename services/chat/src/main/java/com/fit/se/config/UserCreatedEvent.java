package com.fit.se.config;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
        String userId,
        String phone
) {}

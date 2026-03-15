package com.fit.se.api.dto.response;

import lombok.Builder;

@Builder
public record DeviceSessionResponse(
        String deviceId,
        String deviceType,
        String createdAt,
        String lastActiveAt
) {}
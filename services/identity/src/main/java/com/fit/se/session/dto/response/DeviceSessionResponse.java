package com.fit.se.session.dto.response;

import lombok.Builder;

@Builder
public record DeviceSessionResponse(
        String deviceId,
        String deviceType,
        String createdAt,
        String lastActiveAt
) {}

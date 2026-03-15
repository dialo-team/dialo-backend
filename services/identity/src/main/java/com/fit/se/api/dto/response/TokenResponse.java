package com.fit.se.api.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
   String accessToken,
   String refreshToken,
   String tokenType,
   String deviceId,
   String deviceType
) {}

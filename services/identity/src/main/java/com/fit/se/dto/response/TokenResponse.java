package com.fit.se.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
   String accessToken,
   String refreshToken,
   String tokenType,
   String deviceId,
   String deviceType
) {}

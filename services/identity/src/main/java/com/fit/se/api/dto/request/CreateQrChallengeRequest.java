package com.fit.se.api.dto.request;

public record CreateQrChallengeRequest(
    String deviceId,
    String deviceType
) {}

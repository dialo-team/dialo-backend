package com.fit.se.api.dto.response;

public record QRChallengeResponse(
   String challengeId,
   String qrContent,
   long expiresIn
) {}

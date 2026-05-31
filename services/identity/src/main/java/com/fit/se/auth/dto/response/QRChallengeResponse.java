package com.fit.se.auth.dto.response;

public record QRChallengeResponse(
   String challengeId,
   String qrContent,
   long expiresIn
) {}


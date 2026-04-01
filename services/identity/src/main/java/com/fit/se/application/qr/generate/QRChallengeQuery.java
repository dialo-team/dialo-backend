package com.fit.se.application.qr.generate;

import lombok.Builder;

@Builder
public record QRChallengeQuery(
   String ipAddress,
   String agent
) {}

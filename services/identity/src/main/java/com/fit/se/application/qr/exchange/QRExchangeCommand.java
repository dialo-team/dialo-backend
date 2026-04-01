package com.fit.se.application.qr.exchange;

import lombok.Builder;

@Builder
public record QRExchangeCommand(
        String challengeId,
        String ipAddress,
        String agent
) {}

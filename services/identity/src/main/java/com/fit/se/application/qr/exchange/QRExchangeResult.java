package com.fit.se.application.qr.exchange;

import lombok.Builder;

@Builder
public record QRExchangeResult(
        String accessToken,
        String refreshToken,
        String tokenType
) {}

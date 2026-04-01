package com.fit.se.application.qr.approve;

import lombok.Builder;

@Builder
public record QRApproveCommand(
        String challengeId,
        String userId,
        String ipAddress,
        String agent
) {}

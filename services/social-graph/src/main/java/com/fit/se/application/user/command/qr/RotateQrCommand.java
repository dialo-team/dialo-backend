package com.fit.se.application.user.command.qr;

import lombok.Builder;

@Builder
public record RotateQrCommand(
        String current
) {
}
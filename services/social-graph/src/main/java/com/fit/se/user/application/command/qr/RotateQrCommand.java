package com.fit.se.user.application.command.qr;

import lombok.Builder;

@Builder
public record RotateQrCommand(
        String current
) {
}
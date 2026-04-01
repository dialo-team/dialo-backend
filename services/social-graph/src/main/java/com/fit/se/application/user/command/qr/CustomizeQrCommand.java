package com.fit.se.application.user.command.qr;

import lombok.Builder;

@Builder
public record CustomizeQrCommand(
        String current,
        String title,
        String description,
        String color
) {
}
package com.fit.se.user.application.command.qr;

import lombok.Builder;

@Builder
public record CustomizeQrCommand(
        String current,
        String title,
        String description,
        String color
) {
}
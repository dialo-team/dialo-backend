package com.fit.se.application.user.command.background;

import lombok.Builder;

@Builder
public record UpdateBackgroundCommand(
        String current
) {
}
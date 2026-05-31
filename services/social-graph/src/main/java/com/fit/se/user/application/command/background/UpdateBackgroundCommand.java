package com.fit.se.user.application.command.background;

import lombok.Builder;

@Builder
public record UpdateBackgroundCommand(
        String current
) {
}
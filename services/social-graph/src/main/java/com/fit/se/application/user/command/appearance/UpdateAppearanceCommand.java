package com.fit.se.application.user.command.appearance;

import lombok.Builder;

@Builder
public record UpdateAppearanceCommand(
        String current,
        String avatar,
        String background,
        String theme
) {
}
package com.fit.se.user.application.command.appearance;

import lombok.Builder;

@Builder
public record UpdateAppearanceCommand(
        String current,
        String avatar,
        String background,
        String theme
) {
}
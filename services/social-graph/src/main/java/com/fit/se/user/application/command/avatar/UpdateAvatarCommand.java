package com.fit.se.user.application.command.avatar;

import lombok.Builder;

@Builder
public record UpdateAvatarCommand(
        String current
) {
}
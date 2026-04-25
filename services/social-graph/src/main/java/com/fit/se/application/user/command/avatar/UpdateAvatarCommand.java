package com.fit.se.application.user.command.avatar;

import lombok.Builder;

@Builder
public record UpdateAvatarCommand(
        String current
) {
}
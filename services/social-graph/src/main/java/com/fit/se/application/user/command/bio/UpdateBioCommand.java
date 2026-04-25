package com.fit.se.application.user.command.bio;

import lombok.Builder;

@Builder
public record UpdateBioCommand(
        String current,
        String bio
) {
}
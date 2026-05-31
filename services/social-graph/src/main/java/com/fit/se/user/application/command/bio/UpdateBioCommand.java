package com.fit.se.user.application.command.bio;

import lombok.Builder;

@Builder
public record UpdateBioCommand(
        String current,
        String bio
) {
}
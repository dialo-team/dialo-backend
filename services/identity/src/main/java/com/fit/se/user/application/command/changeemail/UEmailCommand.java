package com.fit.se.user.application.command.changeemail;

import lombok.Builder;

@Builder
public record UEmailCommand(
        String userId,
        String newEmail
) {}


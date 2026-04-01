package com.fit.se.application.user.command.changeemail;

import lombok.Builder;

@Builder
public record UEmailCommand(
        String userId,
        String newEmail
) {}

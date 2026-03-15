package com.fit.se.application.user.command.changeemail;

import lombok.Builder;

@Builder
public record ChangeEmailCommand(
        String accId,
        String newEmail
) {}

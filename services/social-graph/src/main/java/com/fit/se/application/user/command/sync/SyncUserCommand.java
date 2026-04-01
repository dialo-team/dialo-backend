package com.fit.se.application.user.command.sync;

import lombok.Builder;

@Builder
public record SyncUserCommand(
        String userId,
        String phone
) {}

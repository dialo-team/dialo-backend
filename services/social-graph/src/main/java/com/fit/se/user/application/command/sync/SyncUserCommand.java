package com.fit.se.user.application.command.sync;

import lombok.Builder;

@Builder
public record SyncUserCommand(
        String userId,
        String phone
) {}

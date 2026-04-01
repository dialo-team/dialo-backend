package com.fit.se.application.user.command.changepass;

import lombok.Builder;

@Builder
public record UPasswordCommand(
    String userId,
    String newPass
) {}

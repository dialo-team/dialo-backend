package com.fit.se.application.user.command.changepass;

import lombok.Builder;

@Builder
public record ChangePasswordCommand(
    String accId,
    String newPass
) {}

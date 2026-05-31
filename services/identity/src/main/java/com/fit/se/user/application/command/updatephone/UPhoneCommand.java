package com.fit.se.user.application.command.updatephone;

import lombok.Builder;

@Builder
public record UPhoneCommand(
        String userId,
        String newPhone
) {
}


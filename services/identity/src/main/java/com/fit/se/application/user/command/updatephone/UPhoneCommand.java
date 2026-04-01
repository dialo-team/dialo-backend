package com.fit.se.application.user.command.updatephone;

import lombok.Builder;

@Builder
public record UPhoneCommand(
        String userId,
        String newPhone
) {
}

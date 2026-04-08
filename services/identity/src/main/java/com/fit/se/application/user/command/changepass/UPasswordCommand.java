package com.fit.se.application.user.command.changepass;

import lombok.Builder;
import org.springframework.security.core.Authentication;

@Builder
public record UPasswordCommand(
        Authentication auth,
        String oldPass,
        String newPass
) {
}

package com.fit.se.user.application.command.changepass;

import lombok.Builder;
import org.springframework.security.core.Authentication;

@Builder
public record UPasswordCommand(
        Authentication auth,
        String oldPass,
        String newPass
) {
}


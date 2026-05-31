package com.fit.se.user.application.command.resetpass;

import lombok.Builder;
import org.springframework.security.core.Authentication;

@Builder
public record ResetPassCommand(
        String newPass,
        Authentication auth
) {
}


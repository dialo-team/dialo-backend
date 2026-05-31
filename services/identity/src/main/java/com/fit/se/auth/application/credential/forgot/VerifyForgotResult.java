package com.fit.se.auth.application.credential.forgot;

import lombok.Builder;

@Builder
public record VerifyForgotResult(
        String resetToken
) {
}


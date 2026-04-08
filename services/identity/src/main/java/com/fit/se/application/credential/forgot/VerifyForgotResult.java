package com.fit.se.application.credential.forgot;

import lombok.Builder;

@Builder
public record VerifyForgotResult(
        String resetToken
) {
}

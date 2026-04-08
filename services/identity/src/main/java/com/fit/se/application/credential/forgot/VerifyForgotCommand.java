package com.fit.se.application.credential.forgot;

import lombok.Builder;

@Builder
public record VerifyForgotCommand(
        String phone,
        String otp
) {
}

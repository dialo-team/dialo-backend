package com.fit.se.application.user.command.verify;

import lombok.Builder;

@Builder
public record VerifyOTPCommand(
    String phone,
    String otp
) {}

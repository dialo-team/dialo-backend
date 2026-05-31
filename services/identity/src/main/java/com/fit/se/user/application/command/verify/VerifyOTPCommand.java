package com.fit.se.user.application.command.verify;

import lombok.Builder;

@Builder
public record VerifyOTPCommand(
    String phone,
    String otp
) {}


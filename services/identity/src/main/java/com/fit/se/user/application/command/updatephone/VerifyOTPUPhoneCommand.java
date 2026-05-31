package com.fit.se.user.application.command.updatephone;

import lombok.Builder;

@Builder
public record VerifyOTPUPhoneCommand(
        String userId,
        String otp
) {}


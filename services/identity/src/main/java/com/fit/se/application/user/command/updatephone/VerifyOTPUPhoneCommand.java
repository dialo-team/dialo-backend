package com.fit.se.application.user.command.updatephone;

import lombok.Builder;

@Builder
public record VerifyOTPUPhoneCommand(
        String userId,
        String otp
) {}

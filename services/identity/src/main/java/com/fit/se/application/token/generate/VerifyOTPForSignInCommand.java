package com.fit.se.application.token.generate;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;

@Builder
public record VerifyOTPForSignInCommand(
   String phone,
   String otp,
   HttpServletRequest httpRequest
) {}

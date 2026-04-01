package com.fit.se.api.dto.request;

public record VerifyOTPRequest(
   String phone,
   String otp
) {}

package com.fit.se.auth.dto.request;

public record OtpValidateRequest(
   String phone,
   String otp
) {}


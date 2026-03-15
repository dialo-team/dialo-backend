package com.fit.se.api.dto.request;

public record OtpValidateRequest(
   String phone,
   String otp
) {}

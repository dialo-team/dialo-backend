package com.fit.se.dto.request;

public record OtpValidateRequest(
   String phone,
   String otp
) {}

package com.fit.se.auth.dto.request;

public record SignUpRequest(
        String phone,
        String password,
        String otp
) {}


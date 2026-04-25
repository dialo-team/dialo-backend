package com.fit.se.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        String phone,
        String password,
        String otp
) {}

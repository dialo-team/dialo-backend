package com.fit.se.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank String phone,
        @NotBlank String password
) {}

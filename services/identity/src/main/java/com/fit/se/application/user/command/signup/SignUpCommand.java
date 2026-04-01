package com.fit.se.application.user.command.signup;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignUpCommand(
        @NotBlank String phone,
        @NotBlank String password
) {}

package com.fit.se.application.user.command.signup;

import jakarta.validation.constraints.NotBlank;

public record SignUpCommand(
        @NotBlank String phone,
        @NotBlank String password
) {}

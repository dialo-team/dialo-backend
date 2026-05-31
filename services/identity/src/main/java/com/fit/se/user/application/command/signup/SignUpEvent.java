package com.fit.se.user.application.command.signup;

import lombok.Builder;

@Builder
public record SignUpEvent(
   String username
) {}


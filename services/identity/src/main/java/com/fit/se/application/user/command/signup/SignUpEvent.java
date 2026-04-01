package com.fit.se.application.user.command.signup;

import lombok.Builder;

@Builder
public record SignUpEvent(
   String username
) {}

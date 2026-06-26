package com.fit.se.auth.application.credential.forgot;

import lombok.Builder;

@Builder
public record ForgotByPhoneCommand(
    String phone
) {}


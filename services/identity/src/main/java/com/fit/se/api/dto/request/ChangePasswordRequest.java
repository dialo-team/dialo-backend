package com.fit.se.api.dto.request;

public record ChangePasswordRequest(
        String newPass,
        String refreshToken
) {}

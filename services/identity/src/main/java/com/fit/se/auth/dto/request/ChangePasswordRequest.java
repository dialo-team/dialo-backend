package com.fit.se.auth.dto.request;

public record ChangePasswordRequest(
        String oldPass,
        String newPass,
        String refreshToken
) {}


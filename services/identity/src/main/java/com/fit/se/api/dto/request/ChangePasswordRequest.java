package com.fit.se.api.dto.request;

public record ChangePasswordRequest(
        String oldPass,
        String newPass,
        String refreshToken
) {}

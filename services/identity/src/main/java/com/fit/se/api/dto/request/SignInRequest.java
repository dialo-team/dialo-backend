package com.fit.se.api.dto.request;

public record SignInRequest(
        String phone,
        String password
) {}

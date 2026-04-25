package com.fit.se.api.dto.request;

public record OldSignInRequest(
        String phone,
        String password
) {
}

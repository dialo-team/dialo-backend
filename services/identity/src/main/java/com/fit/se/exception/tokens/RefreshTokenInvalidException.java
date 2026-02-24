package com.fit.se.exception.tokens;

import com.fit.se.exception.DomainException;

public class RefreshTokenInvalidException extends DomainException {
    public RefreshTokenInvalidException() {
        super(
                401,
                "REFRESH_TOKEN_INVALID",
                "Refresh token is invalid",
                null
        );
    }
}
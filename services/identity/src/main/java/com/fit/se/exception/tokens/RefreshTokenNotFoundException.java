package com.fit.se.exception.tokens;

import com.fit.se.exception.DomainException;

public class RefreshTokenNotFoundException extends DomainException {
    public RefreshTokenNotFoundException() {
        super(
                401,
                "REFRESH_TOKEN_NOT_FOUND",
                "Refresh token not found",
                null
        );
    }
}

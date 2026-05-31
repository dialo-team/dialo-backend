package com.fit.se.common.exception.tokens;

import com.fit.se.common.exception.DomainException;

public class RefreshTokenInvalidException extends DomainException {
    public RefreshTokenInvalidException() {
        super(
                401,
                "REFRESH_TOKEN_INVALID",
                "Refresh token khÃ´ng há»£p lá»‡",
                null
        );
    }
}


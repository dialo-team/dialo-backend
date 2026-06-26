package com.fit.se.common.exception.tokens;

import com.fit.se.common.exception.DomainException;

public class RefreshTokenNotFoundException extends DomainException {
    public RefreshTokenNotFoundException() {
        super(
                401,
                "REFRESH_TOKEN_NOT_FOUND",
                "PhiÃªn Ä‘Äƒng nháº­p Ä‘Ã£ háº¿t háº¡n hoáº·c khÃ´ng cÃ²n tá»“n táº¡i",
                null
        );
    }
}


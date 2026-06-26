package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super(
                401,
                "INVALID_CREDENTIALS",
                "Sá»‘ Ä‘iá»‡n thoáº¡i hoáº·c máº­t kháº©u khÃ´ng chÃ­nh xÃ¡c",
                null
        );
    }
}

package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

public class SessionNotFoundException extends DomainException {
    public SessionNotFoundException() {
        super(
                404,
                "SESSION_NOT_FOUND",
                "PhiÃªn Ä‘Äƒng nháº­p khÃ´ng cÃ²n tá»“n táº¡i",
                null
        );
    }
}


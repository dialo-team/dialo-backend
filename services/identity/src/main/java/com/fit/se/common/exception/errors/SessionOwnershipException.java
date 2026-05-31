package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

public class SessionOwnershipException extends DomainException {
    public SessionOwnershipException() {
        super(
                403,
                "SESSION_OWNERSHIP_INVALID",
                "Báº¡n khÃ´ng cÃ³ quyá»n thao tÃ¡c trÃªn phiÃªn Ä‘Äƒng nháº­p nÃ y",
                null
        );
    }
}


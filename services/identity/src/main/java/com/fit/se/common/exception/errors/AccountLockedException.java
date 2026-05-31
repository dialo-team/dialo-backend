package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

public class AccountLockedException extends DomainException {
    public AccountLockedException() {
        super(
                423,
                "ACCOUNT_LOCKED",
                "TÃ i khoáº£n cá»§a báº¡n Ä‘Ã£ bá»‹ khÃ³a",
                null
        );
    }
}

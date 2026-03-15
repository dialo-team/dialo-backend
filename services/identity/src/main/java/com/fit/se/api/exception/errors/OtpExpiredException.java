package com.fit.se.api.exception.errors;

import com.fit.se.api.exception.DomainException;

public class OtpExpiredException extends DomainException {
    public OtpExpiredException() {
        super(
                401,
                "OTP_EXPIRED",
                "OTP has expired",
                null
        );
    }
}

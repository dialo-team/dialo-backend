package com.fit.se.auth.infrastructure.otp;

import com.fit.se.auth.domain.otp.OtpType;

public interface OtpChannelState {
    OtpType getType();
    void send(String destination, String otp);
}


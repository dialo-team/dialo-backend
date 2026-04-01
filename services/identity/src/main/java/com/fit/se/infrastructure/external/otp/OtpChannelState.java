package com.fit.se.infrastructure.external.otp;

import com.fit.se.domain.otp.OtpType;

public interface OtpChannelState {
    OtpType getType();
    void send(String destination, String otp);
}

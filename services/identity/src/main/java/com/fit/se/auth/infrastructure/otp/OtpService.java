package com.fit.se.auth.infrastructure.otp;

import com.fit.se.auth.domain.otp.OtpType;

public interface OtpService {
    public int send(String destination, OtpType type);
    public void reSend(String phone);
    public boolean validate(String phone, String otp);
}


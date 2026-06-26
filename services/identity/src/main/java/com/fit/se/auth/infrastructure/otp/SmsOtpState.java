package com.fit.se.auth.infrastructure.otp;

import com.fit.se.auth.domain.otp.OtpType;
import com.fit.se.auth.infrastructure.sms.SMSAmazon;
import com.fit.se.auth.infrastructure.sms.SMSSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SmsOtpState implements OtpChannelState {
    private final SMSSender sms;

    @Override
    public OtpType getType() {
        return OtpType.SMS;
    }

    @Override
    public void send(String destination, String otp) {
        sms.send(destination, otp);
        System.out.println("Sending SMS OTP to " + destination);
    }
}


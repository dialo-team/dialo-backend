package com.fit.se.infrastructure.external.otp;

import com.fit.se.domain.otp.OtpType;
import com.fit.se.infrastructure.external.sms.SMSAmazon;
import com.fit.se.infrastructure.external.sms.SMSSender;
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

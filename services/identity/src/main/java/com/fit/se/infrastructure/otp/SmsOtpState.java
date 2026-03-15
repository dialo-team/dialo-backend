package com.fit.se.infrastructure.otp;

import com.fit.se.domain.otp.OtpType;
import org.springframework.stereotype.Component;

@Component
public class SmsOtpState implements OtpChannelState {

    @Override
    public OtpType getType() {
        return OtpType.SMS;
    }

    @Override
    public void send(String destination, String otp) {
        // gọi Twilio
        System.out.println("Sending SMS OTP to " + destination);
    }
}

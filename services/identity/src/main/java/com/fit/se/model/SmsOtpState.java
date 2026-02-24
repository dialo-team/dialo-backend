package com.fit.se.model;

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

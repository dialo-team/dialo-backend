package com.fit.se.model;

public interface OtpChannelState {
    OtpType getType();
    void send(String destination, String otp);
}

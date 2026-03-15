package com.fit.se.infrastructure.otp;

public interface EmailService {
    public void sendEmail(String to, String subject, String body);
}

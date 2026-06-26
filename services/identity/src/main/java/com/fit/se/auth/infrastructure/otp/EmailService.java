package com.fit.se.auth.infrastructure.otp;

public interface EmailService {
    public void sendEmail(String to, String subject, String body);
}


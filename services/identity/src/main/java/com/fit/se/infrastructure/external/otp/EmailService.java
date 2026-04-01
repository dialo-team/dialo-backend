package com.fit.se.infrastructure.external.otp;

public interface EmailService {
    public void sendEmail(String to, String subject, String body);
}

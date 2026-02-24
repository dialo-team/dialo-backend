package com.fit.se.model;

import com.fit.se.repository.AccountRepository;
import com.fit.se.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOtpState implements OtpChannelState {
    private final EmailService emailService;
    private final AccountRepository accountRepo;

    @Override
    public OtpType getType() {
        return OtpType.EMAIL;
    }

    @Override
    public void send(String destination, String otp) {
        Account account = accountRepo.findByPhone(destination)
                .orElseThrow();
        String email = account.getEmail();
        emailService.sendEmail(email, "Verify OTP", otp);
    }
}

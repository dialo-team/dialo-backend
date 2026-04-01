package com.fit.se.infrastructure.external.otp;

import com.fit.se.domain.otp.OtpType;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOtpState implements OtpChannelState {
    private final EmailService emailService;
    private final AccountRepository userRepo;

    @Override
    public OtpType getType() {
        return OtpType.EMAIL;
    }

    @Override
    public void send(String destination, String otp) {
        Account account = userRepo.findByPhone(destination)
                .orElseThrow();
        String email = account.getEmail();
        emailService.sendEmail(email, "Verify OTP", otp);
    }
}

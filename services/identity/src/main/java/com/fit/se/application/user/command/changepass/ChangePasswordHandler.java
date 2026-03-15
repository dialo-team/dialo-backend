package com.fit.se.application.user.command.changepass;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordHandler {
    private final AccountRepository accountRepo;

    public void execute(ChangePasswordCommand cmd) {
        Account acc = accountRepo.findById(cmd.accId())
                .orElseThrow();
        acc.changePassword(cmd.newPass());
        accountRepo.save(acc);
    }
}

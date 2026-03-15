package com.fit.se.application.user.command.changeemail;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeEmailHandler {
    private final AccountRepository accountRepo;

    public void execute(ChangeEmailCommand cmd) {
        Account acc = accountRepo.findById(cmd.accId())
                .orElseThrow();

        acc.changeEmail(cmd.newEmail());

        accountRepo.save(acc);
    }
}

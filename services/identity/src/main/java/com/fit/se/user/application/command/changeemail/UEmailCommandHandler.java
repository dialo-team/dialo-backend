package com.fit.se.user.application.command.changeemail;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UEmailCommandHandler {
    private final AccountRepository userRepo;

    public void execute(UEmailCommand cmd) {
        Account acc = userRepo.findById(cmd.userId())
                .orElseThrow();

        acc.changeEmail(cmd.newEmail());

        userRepo.save(acc);
    }
}


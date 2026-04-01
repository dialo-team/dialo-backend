package com.fit.se.application.user.command.changeemail;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
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

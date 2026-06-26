package com.fit.se.user.application.command.updatephone;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UPhoneCommandHandler {
    private final AccountRepository userRepo;

    public void execute(UPhoneCommand cmd) {
        Account user = userRepo.findById(cmd.userId()).orElseThrow();
        user.changePhone(cmd.newPhone());

    }
}


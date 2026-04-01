package com.fit.se.application.user.command.updatephone;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
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

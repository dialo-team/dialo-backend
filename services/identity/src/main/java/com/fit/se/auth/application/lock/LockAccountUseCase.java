package com.fit.se.auth.application.lock;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockAccountUseCase {
    private final AccountRepository accountRepo;

    public void execute(String accountId) {
        Account account = accountRepo.findById(accountId).orElseThrow();
        account.lock();
        accountRepo.save(account);
    }
}


package com.fit.se.application.lock;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
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

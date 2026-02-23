package com.fit.se.service.impl;

import com.fit.se.dto.request.SignUpRequest;
import com.fit.se.dto.request.UpEmailRequest;
import com.fit.se.dto.request.UpPassRequest;
import com.fit.se.exception.errors.AccountAlreadyExistsException;
import com.fit.se.exception.errors.AccountNotFoundException;
import com.fit.se.mapper.AccountMapper;
import com.fit.se.model.Account;
import com.fit.se.repository.AccountRepository;
import com.fit.se.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepo;
    private final AccountMapper accountMapper;

    @Override
    public void create(SignUpRequest request) {
        if (accountRepo.existsByPhone(request.phone()))
            throw new AccountAlreadyExistsException();
        Account account = accountMapper.toAccount(request);
        accountRepo.save(account);
    }

    @Override
    public void updateEmail(String id, UpEmailRequest request) {
        Account account = accountRepo.findById(id).orElseThrow(AccountNotFoundException::new);
        account.setEmail(request.email());
        accountRepo.save(account);
    }

    @Override
    public void updatePassword(String id, UpPassRequest request) {
        Account account = accountRepo.findById(id).orElseThrow(AccountNotFoundException::new);
        account.setPassword(request.password());
        accountRepo.save(account);
    }

    @Override
    public void activate(String phone) {
        Account account = accountRepo.findByPhone(phone).orElseThrow();
        account.setEnabled(true);
    }
}

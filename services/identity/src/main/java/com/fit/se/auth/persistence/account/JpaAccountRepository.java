package com.fit.se.auth.persistence.account;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAccountRepository implements AccountRepository {
    private final SpringDataAccountRepositoty userRepo;
    private final AccountMapper accountMapper;

    @Override
    public boolean existsByPhone(String phone) {
        return userRepo.existsByPhone(phone);
    }

    @Override
    public Optional<Account> findByPhone(String phone) {
        return userRepo.findByPhone(phone).map(accountMapper::toDomain);
    }

    @Override
    public String save(Account account) {
        return userRepo.save(accountMapper.toEntity(account)).getId();
    }

    @Override
    public void updatePassword(String id, String newPass) {
    }

    @Override
    public void updateEmail(String id, String newEmail) {
        AccountEntity account = userRepo.findById(id).orElseThrow();
        account.setEmail(newEmail);
        userRepo.save(account);
    }

    @Override
    public Optional<Account> findById(String id) {
        return userRepo.findById(id).map(accountMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public Optional<Account> findByEmail(String source) {
        return userRepo.findByEmail(source).map(accountMapper::toDomain);
    }

    @Override
    @Transactional
    public void markProfileProvisioned(String id) {
        AccountEntity account = userRepo.findById(id).orElseThrow();
        account.setProfileProvisioned(true);
        userRepo.save(account);
    }
}

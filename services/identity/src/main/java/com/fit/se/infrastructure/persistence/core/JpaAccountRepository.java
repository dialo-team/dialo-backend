package com.fit.se.infrastructure.persistence.core;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
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
        return this.userRepo.save(accountMapper.toEntity(account)).getId();
    }

    @Override
    public void updatePassword(String id, String newPass) {

    }

    @Override
    public void updateEmail(String id, String newEmail) {
        AccountEntity account = userRepo.findById(id).orElseThrow();


    }

    @Override
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountMapper.toDomain(userRepo.findById(id).orElseThrow()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public Optional<Account> findByEmail(String source) {
        return userRepo.findByEmail(source).map(accountMapper::toDomain);
    }


}

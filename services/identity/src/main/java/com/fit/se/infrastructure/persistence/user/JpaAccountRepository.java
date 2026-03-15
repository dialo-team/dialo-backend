package com.fit.se.infrastructure.persistence.user;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAccountRepository implements AccountRepository {
    private final SpringDataAccountRepositoty accountRepo;
    private final AccountMapper accountMapper;

    @Override
    public boolean existsByPhone(String phone) {
        return false;
    }

    @Override
    public Optional<Account> findByPhone(String phone) {
        return Optional.empty();
    }

    @Override
    public void save(Account account) {
        this.accountRepo.save(accountMapper.toEntity(account));
    }

    @Override
    public void updatePassword(String id, String newPass) {

    }

    @Override
    public void updateEmail(String id, String newEmail) {
        AccountEntity account = accountRepo.findById(id).orElseThrow();


    }

    @Override
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountMapper.toDomain(accountRepo.findById(id).orElseThrow()));
    }


}

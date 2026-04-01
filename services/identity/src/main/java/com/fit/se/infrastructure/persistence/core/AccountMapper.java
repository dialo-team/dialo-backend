package com.fit.se.infrastructure.persistence.core;

import com.fit.se.domain.user.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final PasswordEncoder passwordEncoder;

//    public Account toAccount(SignUpRequest request) {
//        return Account.builder()
//                .phone(request.phone())
//                .password(passwordEncoder.encode(request.password()))
//                .enabled(false)
//                .locked(false)
//                .build();
//    }

    public AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
                .phone(account.getPhone())
                .enabled(false)
                .locked(false)
                .build();
    }

    public Account toDomain(AccountEntity entity) {
        Account domain = Account.create(entity.getPhone());
        domain.setId(entity.getId());
        return domain;
    }
}

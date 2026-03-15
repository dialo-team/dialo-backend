package com.fit.se.infrastructure.persistence.user;

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
                .password(account.getPassword())
                .enabled(false)
                .locked(false)
                .build();
    }

    public Account toDomain(AccountEntity account) {
        return Account.builder()
                .phone(account.getPhone())
                .password(account.getPassword())
                .enabled(account.isEnabled())
                .locked(account.isLocked())
                .build();
    }
}

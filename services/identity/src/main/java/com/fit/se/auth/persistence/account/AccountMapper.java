package com.fit.se.auth.persistence.account;

import com.fit.se.auth.domain.account.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
                .id(account.getId())
                .phone(account.getPhone())
                .email(account.getEmail())
                .enabled(account.isEnabled())
                .locked(account.isLocked())
                .profileProvisioned(account.isProfileProvisioned())
                .build();
    }

    public Account toDomain(AccountEntity entity) {
        boolean profileProvisioned = entity.getProfileProvisioned() == null || entity.getProfileProvisioned();
        return Account.reconstitute(
                entity.getId(),
                entity.getPhone(),
                entity.getEmail(),
                entity.isLocked(),
                entity.isEnabled(),
                profileProvisioned
        );
    }
}

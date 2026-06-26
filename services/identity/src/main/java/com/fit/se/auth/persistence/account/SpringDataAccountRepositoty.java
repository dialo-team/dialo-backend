package com.fit.se.auth.persistence.account;

import com.fit.se.auth.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface SpringDataAccountRepositoty extends JpaRepository<AccountEntity, String> {
    boolean existsByPhone(String phone);

    Optional<AccountEntity> findByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<AccountEntity> findByEmail(String email);
}


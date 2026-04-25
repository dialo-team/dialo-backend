package com.fit.se.infrastructure.persistence.core;

import com.fit.se.domain.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface SpringDataAccountRepositoty extends JpaRepository<AccountEntity, String> {
    boolean existsByPhone(String phone);

    Optional<AccountEntity> findByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<AccountEntity> findByEmail(String email);
}

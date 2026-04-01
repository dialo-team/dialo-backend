package com.fit.se.infrastructure.persistence.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAccountRepositoty extends JpaRepository<AccountEntity, String> {
    boolean existsByPhone(String phone);

    Optional<AccountEntity> findByPhone(String phone);
}

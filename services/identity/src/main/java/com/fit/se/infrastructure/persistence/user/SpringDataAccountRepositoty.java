package com.fit.se.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAccountRepositoty extends JpaRepository<AccountEntity, String> {
    boolean existsByPhone(String phone);

    Optional<AccountEntity> findByPhone(String phone);
}

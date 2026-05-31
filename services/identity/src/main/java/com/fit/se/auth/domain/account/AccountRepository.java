package com.fit.se.auth.domain.account;

import java.util.Optional;

public interface AccountRepository {
    boolean existsByPhone(String phone);

    Optional<Account> findByPhone(String phone);

    String save(Account account);

    void updatePassword(String id, String newPass);

    void updateEmail(String id, String newEmail);

    Optional<Account> findById(String id);

    boolean existsByEmail(String source);

    Optional<Account> findByEmail(String source);

    void markProfileProvisioned(String id);
}

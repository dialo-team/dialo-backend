package com.fit.se.domain.user;


import java.util.Optional;

public interface AccountRepository {
    boolean existsByPhone(String phone);

    Optional<Account> findByPhone(String phone);

    String save(Account account);

    void updatePassword(String id, String newPass);

    void updateEmail(String id, String newEmail);

    Optional<Account> findById(String id);
}

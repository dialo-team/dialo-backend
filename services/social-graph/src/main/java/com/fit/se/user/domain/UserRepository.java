package com.fit.se.user.domain;

import com.fit.se.user.domain.aggregate.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);

    public void save(User newUser);

    boolean existsById(String id);

    Optional<User> findByPhone(String phone);
    Optional<User> findByQrToken(String qrToken);
}

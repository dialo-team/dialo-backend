package com.fit.se.domain.person;

import org.apache.catalina.User;

import java.util.Optional;

public interface PersonRepository {
    Optional<User> findById(String id);
}

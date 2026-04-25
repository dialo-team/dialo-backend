package com.fit.se.infrastructure.persistence.repository.adapter;

import com.fit.se.domain.person.PersonRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PersonAdapter implements PersonRepository {
    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }
}

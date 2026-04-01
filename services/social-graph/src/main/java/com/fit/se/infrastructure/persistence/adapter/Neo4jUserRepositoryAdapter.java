package com.fit.se.infrastructure.persistence.adapter;

import com.fit.se.domain.user.aggregate.User;
import com.fit.se.domain.user.UserRepository;
import com.fit.se.infrastructure.persistence.user.UserPersistenceMapper;
import com.fit.se.infrastructure.persistence.user.SpringDataUserNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class Neo4jUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserNeo4jRepository repository;

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public void save(User user) {
        repository.save(UserPersistenceMapper.toNode(user));
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<User> findByQrToken(String qrToken) {
        return repository.findByQrToken(qrToken).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return repository.findByPhone(phone)
                .map(UserPersistenceMapper::toDomain);
    }
}
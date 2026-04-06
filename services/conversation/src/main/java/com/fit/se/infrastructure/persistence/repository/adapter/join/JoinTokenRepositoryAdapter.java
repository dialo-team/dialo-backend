package com.fit.se.infrastructure.persistence.repository.adapter.join;

import com.fit.se.domain.join.repository.JoinTokenRepository;
import com.fit.se.infrastructure.persistence.entity.join.JoinTokenEntity;
import com.fit.se.infrastructure.persistence.repository.jpa.join.SpringDataJoinTokenJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JoinTokenRepositoryAdapter implements JoinTokenRepository {

    private final SpringDataJoinTokenJpaRepository repository;

    public JoinTokenRepositoryAdapter(SpringDataJoinTokenJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(UUID conversationId, String token) {
        JoinTokenEntity entity = new JoinTokenEntity();
        entity.setConversationId(conversationId);
        entity.setToken(token);
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        repository.save(entity);
    }

    @Override
    public Optional<String> findByConversationId(UUID conversationId) {
        return repository.findById(conversationId).filter(JoinTokenEntity::isActive).map(JoinTokenEntity::getToken);
    }

    @Override
    public Optional<UUID> findConversationIdByToken(String token) {
        return repository.findByToken(token).filter(JoinTokenEntity::isActive).map(JoinTokenEntity::getConversationId);
    }
}

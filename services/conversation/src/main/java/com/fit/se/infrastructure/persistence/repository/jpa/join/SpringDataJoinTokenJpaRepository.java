package com.fit.se.infrastructure.persistence.repository.jpa.join;

import com.fit.se.infrastructure.persistence.entity.join.JoinTokenEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJoinTokenJpaRepository extends JpaRepository<JoinTokenEntity, UUID> {

    Optional<JoinTokenEntity> findByToken(String token);

}

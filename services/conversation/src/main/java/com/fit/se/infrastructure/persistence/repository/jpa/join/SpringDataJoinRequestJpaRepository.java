package com.fit.se.infrastructure.persistence.repository.jpa.join;

import com.fit.se.infrastructure.persistence.entity.join.JoinRequestEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJoinRequestJpaRepository extends JpaRepository<JoinRequestEntity, UUID> {

    List<JoinRequestEntity> findAllByConversationId(UUID conversationId);

}

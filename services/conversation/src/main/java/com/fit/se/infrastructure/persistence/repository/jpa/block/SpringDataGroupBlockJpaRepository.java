package com.fit.se.infrastructure.persistence.repository.jpa.block;

import com.fit.se.infrastructure.persistence.entity.block.GroupBlockEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataGroupBlockJpaRepository extends JpaRepository<GroupBlockEntity, UUID> {

    List<GroupBlockEntity> findAllByConversationId(UUID conversationId);

    Optional<GroupBlockEntity> findByConversationIdAndBlockedUserId(UUID conversationId, Long blockedUserId);

}

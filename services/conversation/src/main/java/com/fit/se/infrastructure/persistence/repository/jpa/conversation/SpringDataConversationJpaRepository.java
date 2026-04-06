package com.fit.se.infrastructure.persistence.repository.jpa.conversation;

import com.fit.se.infrastructure.persistence.entity.conversation.ConversationEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataConversationJpaRepository extends JpaRepository<ConversationEntity, UUID> {

}

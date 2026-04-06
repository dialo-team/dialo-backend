package com.fit.se.infrastructure.persistence.repository.jpa.membership;

import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberEntity;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberIdEmbeddable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataConversationMemberJpaRepository extends JpaRepository<ConversationMemberEntity, ConversationMemberIdEmbeddable> {

    List<ConversationMemberEntity> findAllByIdConversationId(UUID conversationId);

    Optional<ConversationMemberEntity> findByIdConversationIdAndIdUserId(UUID conversationId, Long userId);

}

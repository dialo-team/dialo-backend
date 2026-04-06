package com.fit.se.infrastructure.persistence.repository.jpa.label;

import com.fit.se.infrastructure.persistence.entity.label.UserConversationLabelEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserConversationLabelJpaRepository extends JpaRepository<UserConversationLabelEntity, Long> {

    List<UserConversationLabelEntity> findAllByUserId(Long userId);

}

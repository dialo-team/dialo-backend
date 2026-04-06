package com.fit.se.infrastructure.persistence.outbox.repository;

import com.fit.se.infrastructure.persistence.outbox.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataOutboxJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findAllByPublishedFalse();
}

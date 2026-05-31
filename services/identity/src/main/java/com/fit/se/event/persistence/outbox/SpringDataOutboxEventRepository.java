package com.fit.se.event.persistence.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {
    List<OutboxEventEntity> findTop20ByPublishedFalseOrderByCreatedAtAsc();
}

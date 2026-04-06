package com.fit.se.infrastructure.persistence.outbox.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.se.application.port.output.messaging.PublishDomainEventPort;
import com.fit.se.infrastructure.persistence.outbox.entity.OutboxEventEntity;
import com.fit.se.infrastructure.persistence.outbox.repository.SpringDataOutboxJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OutboxEventPublisherAdapter implements PublishDomainEventPort {

    private final SpringDataOutboxJpaRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisherAdapter(SpringDataOutboxJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String eventType, Object payload) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setId(UUID.randomUUID());
        entity.setEventType(eventType);
        entity.setAggregateId(null);
        entity.setPayload(serialize(payload));
        entity.setPublished(false);
        entity.setCreatedAt(Instant.now());
        repository.save(entity);
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize outbox payload", e);
        }
    }
}

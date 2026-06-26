package com.fit.se.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.se.event.model.UserCreatedEvent;
import com.fit.se.event.producer.UserEventPublisher;
import com.fit.se.event.persistence.outbox.OutboxEventEntity;
import com.fit.se.event.persistence.outbox.SpringDataOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxUserEventPublisher implements UserEventPublisher {
    private final SpringDataOutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        outboxRepository.save(OutboxEventEntity.builder()
                .aggregateType("ACCOUNT")
                .aggregateId(event.userId())
                .eventType("USER_CREATED")
                .exchangeName("user.exchange")
                .routingKey("user.created")
                .payload(serialize(event))
                .published(false)
                .publishAttempts(0)
                .build());
    }

    private String serialize(UserCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("KhÃ´ng thá»ƒ ghi user.created vÃ o outbox", ex);
        }
    }
}

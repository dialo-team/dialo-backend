package com.fit.se.event.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.se.event.model.UserCreatedEvent;
import com.fit.se.event.persistence.outbox.OutboxEventEntity;
import com.fit.se.event.persistence.outbox.SpringDataOutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisherJob {
    private final SpringDataOutboxEventRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.outbox.publish-delay:1000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findTop20ByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEventEntity event : pendingEvents) {
            try {
                rabbitTemplate.convertAndSend(
                        event.getExchangeName(),
                        event.getRoutingKey(),
                        deserializePayload(event)
                );
                event.setPublished(true);
                event.setPublishedAt(Instant.now());
                event.setLastError(null);
            } catch (Exception ex) {
                event.setLastError(truncateError(ex.getMessage()));
            }
            event.setPublishAttempts(event.getPublishAttempts() + 1);
            outboxRepository.save(event);
        }
    }

    private Object deserializePayload(OutboxEventEntity event) throws Exception {
        return switch (event.getEventType()) {
            case "USER_CREATED" -> objectMapper.readValue(event.getPayload(), UserCreatedEvent.class);
            default -> throw new IllegalStateException("Unsupported outbox event type: " + event.getEventType());
        };
    }

    private String truncateError(String error) {
        if (error == null || error.isBlank()) {
            return "UNKNOWN_ERROR";
        }
        return error.length() <= 2000 ? error : error.substring(0, 2000);
    }
}

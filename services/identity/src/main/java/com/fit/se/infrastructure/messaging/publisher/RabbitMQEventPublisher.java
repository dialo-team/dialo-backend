package com.fit.se.infrastructure.messaging.publisher;

import com.fit.se.application.user.event.UserCreatedEvent;
import com.fit.se.application.user.event.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements UserEventPublisher {
    private final RabbitTemplate template;
    private final TopicExchange exchange;


    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        template.convertAndSend(
                exchange.getName(),
                "user.created",
                event
        );
    }
}

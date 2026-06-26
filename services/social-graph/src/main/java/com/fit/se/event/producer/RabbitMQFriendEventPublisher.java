package com.fit.se.event.producer;

import com.fit.se.friendship.application.command.accept.FriendAcceptedEvent;
import com.fit.se.event.topology.FriendTopology;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQFriendEventPublisher implements FriendEventPublisher {

    private final RabbitTemplate template;
    private final TopicExchange exchange;

    public RabbitMQFriendEventPublisher(
            RabbitTemplate template,
            @Qualifier("socialEventsExchange") TopicExchange exchange
    ) {
        this.template = template;
        this.exchange = exchange;
    }

    @Override
    public void publishFriendAccepted(FriendAcceptedEvent event) {
        template.convertAndSend(
                exchange.getName(),
                FriendTopology.FRIEND_ACCEPTED_KEY,
                event
        );
    }
}
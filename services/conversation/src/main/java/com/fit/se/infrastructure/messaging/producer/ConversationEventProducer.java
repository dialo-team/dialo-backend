package com.fit.se.infrastructure.messaging.producer;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConversationEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange userTopicExchange;

    public ConversationEventProducer(RabbitTemplate rabbitTemplate, TopicExchange userTopicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.userTopicExchange = userTopicExchange;
    }

    public void publish(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(userTopicExchange.getName(), routingKey, payload);
    }
}

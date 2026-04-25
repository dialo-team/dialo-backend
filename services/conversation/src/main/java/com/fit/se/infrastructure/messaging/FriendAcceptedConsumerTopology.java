package com.fit.se.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FriendAcceptedConsumerTopology {

    public static final String SOCIAL_EVENTS_EXCHANGE = "social.events";
    public static final String FRIEND_ACCEPTED_KEY = "friend.accepted";
    public static final String FRIEND_ACCEPTED_QUEUE = "conversation.friend.accepted.queue";

    @Bean
    public TopicExchange socialEventsExchange() {
        return new TopicExchange(SOCIAL_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue friendAcceptedQueue() {
        return new Queue(FRIEND_ACCEPTED_QUEUE, true);
    }

    @Bean
    public Binding friendAcceptedBinding(
            Queue friendAcceptedQueue,
            @Qualifier("socialEventsExchange") TopicExchange socialEventsExchange
    ) {
        return BindingBuilder
                .bind(friendAcceptedQueue)
                .to(socialEventsExchange)
                .with(FRIEND_ACCEPTED_KEY);
    }
}
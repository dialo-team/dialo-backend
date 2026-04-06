package com.fit.se.infrastructure.messaging.topology;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserCreatedTopology {

    public static final String EXCHANGE = "user.exchange";
    public static final String USER_CREATED_QUEUE = "conversation.user-created.queue";
    public static final String USER_CREATED_KEY = "user.created";

    @Bean
    public TopicExchange userTopicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE);
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userTopicExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(userTopicExchange).with(USER_CREATED_KEY);
    }
}

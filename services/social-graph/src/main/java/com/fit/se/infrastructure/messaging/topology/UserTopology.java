package com.fit.se.infrastructure.messaging.topology;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTopology {

    private static final String EXCHANGE = "user.exchange";
    private static final String USER_CREATED_QUEUE = "user.exchange.queue";
    private static final String USER_CREATED_KEY = "user.created";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE);
    }

    @Bean
    public Binding userCreatedBinding(
            @Qualifier("userCreatedQueue") Queue queue,
            @Qualifier("topicExchange") TopicExchange exchange
    ) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(USER_CREATED_KEY);
    }
}

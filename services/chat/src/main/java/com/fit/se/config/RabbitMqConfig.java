package com.fit.se.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    @Bean
    public Queue friendAcceptedQueue() {
        return new Queue("conversation.friend.accepted", true);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue("user.created", true);
    }

    @Bean
    public TopicExchange socialEventsExchange() {
        return new TopicExchange("social.events", true, false);
    }

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange("user.exchange", true, false);
    }

    @Bean
    public Binding friendAcceptedBinding(Queue friendAcceptedQueue, TopicExchange socialEventsExchange) {
        return BindingBuilder.bind(friendAcceptedQueue)
                .to(socialEventsExchange)
                .with("friend.accepted");
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userCreatedQueue)
                .to(userEventsExchange)
                .with("user.created");
    }

    @Bean
    public JacksonJsonMessageConverter jackson2JsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
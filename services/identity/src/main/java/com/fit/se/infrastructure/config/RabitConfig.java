package com.fit.se.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabitConfig {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("user.exchange");
    }
}

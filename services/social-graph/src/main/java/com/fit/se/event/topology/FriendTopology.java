package com.fit.se.event.topology;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FriendTopology {

    public static final String SOCIAL_EVENTS_EXCHANGE = "social.events";
    public static final String FRIEND_ACCEPTED_KEY = "friend.accepted";

    @Bean
    public TopicExchange socialEventsExchange() {
        return new TopicExchange(SOCIAL_EVENTS_EXCHANGE);
    }
}

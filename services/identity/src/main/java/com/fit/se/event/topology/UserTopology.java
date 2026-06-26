package com.fit.se.event.topology;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTopology {
    public static final String EXCHANGE = "user.exchange";
    public static final String USER_CREATED_KEY = "user.created";
    public static final String USER_PROFILE_PROVISIONED_KEY = "user.profile.provisioned";
    public static final String IDENTITY_USER_PROFILE_PROVISIONED_QUEUE = "identity.user.profile.provisioned.queue";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue identityUserProfileProvisionedQueue() {
        return new Queue(IDENTITY_USER_PROFILE_PROVISIONED_QUEUE, true);
    }

    @Bean
    public Binding identityUserProfileProvisionedBinding(
            @Qualifier("identityUserProfileProvisionedQueue") Queue queue,
            @Qualifier("topicExchange") TopicExchange exchange
    ) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(USER_PROFILE_PROVISIONED_KEY);
    }
}

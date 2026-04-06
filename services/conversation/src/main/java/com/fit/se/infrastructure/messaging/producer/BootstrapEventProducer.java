package com.fit.se.infrastructure.messaging.producer;

import org.springframework.stereotype.Component;

@Component
public class BootstrapEventProducer {

    private final ConversationEventProducer producer;

    public BootstrapEventProducer(ConversationEventProducer producer) {
        this.producer = producer;
    }

    public void publishBootstrapCompleted(Object payload) {
        producer.publish("conversation.bootstrap.completed", payload);
    }
}

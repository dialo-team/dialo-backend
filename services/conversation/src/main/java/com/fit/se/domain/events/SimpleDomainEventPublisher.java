package com.fit.se.domain.events;

import java.util.ArrayList;
import java.util.List;

public class SimpleDomainEventPublisher implements DomainEventPublisher {
    private final List<DomainEventSubscriber> subscribers = new ArrayList<>();

    public void subscribe(DomainEventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void publish(DomainEvent event) {
        for (DomainEventSubscriber subscriber : subscribers) {
            subscriber.handle(event);
        }
    }
}

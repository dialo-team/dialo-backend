package com.fit.se.domain.events;

public class LoggingSubscriber implements DomainEventSubscriber {
    @Override
    public void handle(DomainEvent event) {
        System.out.println("EVENT: " + event.eventName());
    }
}

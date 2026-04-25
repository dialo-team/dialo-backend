package com.fit.se.domain.events;

public interface DomainEventSubscriber {
    void handle(DomainEvent event);
}

package com.fit.se.domain.events;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}

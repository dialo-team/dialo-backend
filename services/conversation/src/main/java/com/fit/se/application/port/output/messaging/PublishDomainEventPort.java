package com.fit.se.application.port.output.messaging;

public interface PublishDomainEventPort {
    void publish(String eventType, Object payload);
}

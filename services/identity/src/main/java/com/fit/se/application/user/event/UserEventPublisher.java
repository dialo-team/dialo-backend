package com.fit.se.application.user.event;

public interface UserEventPublisher {
    void publishUserCreated(UserCreatedEvent event);
}

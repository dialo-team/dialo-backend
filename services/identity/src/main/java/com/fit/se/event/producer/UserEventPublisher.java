package com.fit.se.event.producer;

import com.fit.se.event.model.UserCreatedEvent;

public interface UserEventPublisher {
    void publishUserCreated(UserCreatedEvent event);
}


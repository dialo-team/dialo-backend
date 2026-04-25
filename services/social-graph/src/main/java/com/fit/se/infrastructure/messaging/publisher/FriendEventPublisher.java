package com.fit.se.infrastructure.messaging.publisher;

import com.fit.se.application.friendship.command.accept.FriendAcceptedEvent;

public interface FriendEventPublisher {
    void publishFriendAccepted(FriendAcceptedEvent event);
}
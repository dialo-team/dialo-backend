package com.fit.se.event.producer;

import com.fit.se.friendship.application.command.accept.FriendAcceptedEvent;

public interface FriendEventPublisher {
    void publishFriendAccepted(FriendAcceptedEvent event);
}
package com.fit.se.friendship.application.query.pending;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PendingFriendRequestView(
        String friendshipId,
        String senderId,
        String senderUserName,
        String senderAvatar,
        String receiverId,
        String receiverUserName,
        String receiverAvatar,
        String reason,
        Instant requestedAt
) {
}
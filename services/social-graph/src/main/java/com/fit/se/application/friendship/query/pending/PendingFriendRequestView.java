package com.fit.se.application.friendship.query.pending;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PendingFriendRequestView(
        String friendshipId,
        String senderId,
        String receiverId,
        Instant requestedAt
) {
}
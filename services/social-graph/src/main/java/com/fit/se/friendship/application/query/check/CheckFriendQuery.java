package com.fit.se.friendship.application.query.check;

import lombok.Builder;

@Builder
public record CheckFriendQuery(
        String currentUserId,
        String targetUserId
) {
}
package com.fit.se.application.friendship.query.pending;

import lombok.Builder;

@Builder
public record MyPendingFriendRequestsQuery(
        String current
) {
}
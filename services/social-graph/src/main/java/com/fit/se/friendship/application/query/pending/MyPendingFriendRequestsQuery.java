package com.fit.se.friendship.application.query.pending;

import lombok.Builder;

@Builder
public record MyPendingFriendRequestsQuery(
        String current
) {
}
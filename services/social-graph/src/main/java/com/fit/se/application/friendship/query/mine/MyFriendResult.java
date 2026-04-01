package com.fit.se.application.friendship.query.mine;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MyFriendResult(
        List<Item> friends
) {
    @Builder
    public record Item(
            String friendshipId,
            String friendId,
            String friendUserName,
            String friendAvatar,
            Instant since
    ) {
    }
}
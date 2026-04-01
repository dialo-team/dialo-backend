package com.fit.se.application.friendship.command.cancel;

import lombok.Builder;

@Builder
public record CancelFriendCommand(
        String senderId,
        String receiverId
) {
}

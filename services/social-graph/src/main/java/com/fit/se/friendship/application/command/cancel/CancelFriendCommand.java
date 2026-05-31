package com.fit.se.friendship.application.command.cancel;

import lombok.Builder;

@Builder
public record CancelFriendCommand(
        String senderId,
        String receiverId
) {
}

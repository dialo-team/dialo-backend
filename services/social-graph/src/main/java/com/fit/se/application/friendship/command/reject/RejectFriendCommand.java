package com.fit.se.application.friendship.command.reject;

import lombok.Builder;

@Builder
public record RejectFriendCommand(
        String senderId,
        String receiverId
) {}

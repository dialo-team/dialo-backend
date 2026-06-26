package com.fit.se.friendship.application.command.reject;

import lombok.Builder;

@Builder
public record RejectFriendCommand(
        String senderId,
        String receiverId
) {}

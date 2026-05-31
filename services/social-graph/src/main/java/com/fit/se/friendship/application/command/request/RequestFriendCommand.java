package com.fit.se.friendship.application.command.request;

import lombok.Builder;

@Builder
public record RequestFriendCommand(
        String friendshipId,
        String senderId,
        String receiverId,
        String reason,
        String source
) {}
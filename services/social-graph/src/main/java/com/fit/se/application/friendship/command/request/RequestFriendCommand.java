package com.fit.se.application.friendship.command.request;

import lombok.Builder;

@Builder
public record RequestFriendCommand(
        String friendshipId,
        String senderId,
        String receiverId
) {}

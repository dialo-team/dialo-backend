package com.fit.se.application.friendship.command.accept;

import lombok.Builder;

@Builder
public record AcceptFriendCommand(
        String senderId,
        String receiverId
) {}

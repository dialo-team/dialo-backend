package com.fit.se.friendship.application.command.accept;

import lombok.Builder;

@Builder
public record AcceptFriendCommand(
        String senderId,
        String receiverId
) {}

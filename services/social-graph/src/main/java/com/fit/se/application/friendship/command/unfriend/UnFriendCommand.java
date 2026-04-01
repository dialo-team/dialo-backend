package com.fit.se.application.friendship.command.unfriend;

import lombok.Builder;

@Builder
public record UnFriendCommand(
        String userAId,
        String userBId
) {}

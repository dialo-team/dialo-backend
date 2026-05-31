package com.fit.se.friendship.application.command.unfriend;

import lombok.Builder;

@Builder
public record UnFriendCommand(
        String userAId,
        String userBId
) {}

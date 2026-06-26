package com.fit.se.friendship.application.query.check;

import lombok.Builder;

@Builder
public record CheckFriendResult(
        String currentUserId,
        String targetUserId,
        String relationStatus,
        boolean isSelf,
        boolean canAddFriend,
        boolean canCancelRequest,
        boolean canAcceptRequest,
        boolean canRejectRequest,
        boolean canMessage,
        boolean canCall,
        boolean canUnfriend,
        boolean blocked
) {
}
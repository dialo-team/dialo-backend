package com.fit.se.user.application.query.info;

import lombok.Builder;

@Builder
public record InfoResult(
        String id,
        String userName,
        String bio,
        String avatar,
        String background,
        String theme,
        String relationStatus,
        boolean isSelf,
        boolean canAddFriend,
        boolean canCancelRequest,
        boolean canAcceptRequest,
        boolean canRejectRequest,
        boolean canMessage,
        boolean canCall,
        boolean canUnfriend
) {
}
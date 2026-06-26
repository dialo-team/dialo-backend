package com.fit.se.user.domain.policy;

import com.fit.se.blocks.domain.aggregate.BlockRelation;
import com.fit.se.friendship.domain.aggregate.Friendship;

public class RelationContext {
    private final Friendship friendship;
    private final BlockRelation blockRelation;

    public RelationContext(Friendship friendship, BlockRelation blockRelation) {
        this.friendship = friendship;
        this.blockRelation = blockRelation;
    }

    public Friendship friendship() {
        return friendship;
    }

    public BlockRelation blockRelation() {
        return blockRelation;
    }

    public boolean areFriends() {
        return friendship != null && friendship.isAccepted();
    }

    public boolean isBlockedBetween(String viewerId, String ownerId) {
        return blockRelation != null && blockRelation.blocks(viewerId, ownerId);
    }
}
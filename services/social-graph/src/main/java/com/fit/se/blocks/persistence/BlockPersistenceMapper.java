package com.fit.se.blocks.persistence;

import com.fit.se.blocks.domain.aggregate.BlockRelation;
import com.fit.se.blocks.domain.valueobject.BlockSource;
import com.fit.se.blocks.domain.valueobject.BlockStatus;
import com.fit.se.user.persistence.node.UserNode;
import com.fit.se.blocks.persistence.relationship.BlockRelationship;

public final class BlockPersistenceMapper {

    private BlockPersistenceMapper() {
    }

    public static BlockRelationship toRelationship(BlockRelation blockRelation, UserNode targetUser) {
        BlockRelationship relationship = new BlockRelationship(targetUser);
        relationship.setBlockId(blockRelation.getId());
        relationship.setBlockerId(blockRelation.getBlockerId());
        relationship.setBlockedId(blockRelation.getBlockedId());
        relationship.setStatus(blockRelation.getStatus().name());
        relationship.setCreatedAt(blockRelation.getCreatedAt());
        relationship.setUnblockedAt(blockRelation.getUnblockedAt());
        relationship.setReason(blockRelation.getReason());
        relationship.setSource(blockRelation.getSource().name());
        return relationship;
    }

    public static BlockRelation toDomain(BlockRelationship relationship) {
        BlockRelation blockRelation = new BlockRelation(
                relationship.getBlockId(),
                relationship.getBlockerId(),
                relationship.getBlockedId(),
                relationship.getCreatedAt(),
                relationship.getReason(),
                BlockSource.valueOf(relationship.getSource())
        );

        if (BlockStatus.valueOf(relationship.getStatus()) == BlockStatus.UNBLOCKED) {
            blockRelation.unblock(relationship.getUnblockedAt());
        }

        return blockRelation;
    }
}
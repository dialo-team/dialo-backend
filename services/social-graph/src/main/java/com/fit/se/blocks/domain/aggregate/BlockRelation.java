package com.fit.se.blocks.domain.aggregate;

import com.fit.se.blocks.domain.valueobject.BlockSource;
import com.fit.se.blocks.domain.valueobject.BlockStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class BlockRelation {
    private final String id;
    private final String blockerId;
    private final String blockedId;

    private BlockStatus status;
    private final Instant createdAt;
    private Instant unblockedAt;

    private final String reason;
    private final BlockSource source;

    public BlockRelation(
            String id,
            String blockerId,
            String blockedId,
            Instant createdAt,
            String reason,
            BlockSource source
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Block id must not be blank");
        }
        if (blockerId == null || blockerId.isBlank()) {
            throw new IllegalArgumentException("Blocker id must not be blank");
        }
        if (blockedId == null || blockedId.isBlank()) {
            throw new IllegalArgumentException("Blocked id must not be blank");
        }
        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("A user cannot block themselves");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created time must not be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("Block source must not be null");
        }

        this.id = id;
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.status = BlockStatus.ACTIVE;
        this.createdAt = createdAt;
        this.reason = reason;
        this.source = source;
    }

    public void unblock(Instant at) {
        if (status != BlockStatus.ACTIVE) {
            throw new IllegalStateException("Only active block can be unblocked");
        }
        if (at == null) {
            throw new IllegalArgumentException("Unblock time must not be null");
        }

        this.status = BlockStatus.UNBLOCKED;
        this.unblockedAt = at;
    }

    public boolean isActive() {
        return status == BlockStatus.ACTIVE;
    }

    public boolean involves(String userId) {
        return blockerId.equals(userId) || blockedId.equals(userId);
    }

    public boolean blocks(String viewerId, String ownerId) {
        if (!isActive()) {
            return false;
        }

        return (blockerId.equals(viewerId) && blockedId.equals(ownerId))
                || (blockerId.equals(ownerId) && blockedId.equals(viewerId));
    }
}
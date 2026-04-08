package com.fit.se.domain.block.aggregate;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;

import java.util.Objects;
import java.util.Optional;

public class GroupBlock {
    private final GroupBlockId id;
    private final ConversationId conversationId;
    private final UserId blockedUserId;
    private final UserId blockedByUserId;
    private final BlockReason reason;

    private GroupBlock(GroupBlockId id, ConversationId conversationId, UserId blockedUserId, UserId blockedByUserId, BlockReason reason) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId must not be null");
        this.blockedUserId = Objects.requireNonNull(blockedUserId, "blockedUserId must not be null");
        this.blockedByUserId = Objects.requireNonNull(blockedByUserId, "blockedByUserId must not be null");
        this.reason = reason;
    }

    public static GroupBlock of(ConversationId conversationId, UserId blockedUserId, UserId blockedByUserId, BlockReason reason) {
        return new GroupBlock(GroupBlockId.newId(), conversationId, blockedUserId, blockedByUserId, reason);
    }

    public GroupBlockId getId() {
        return id;
    }

    public ConversationId getConversationId() {
        return conversationId;
    }

    public UserId getBlockedUserId() {
        return blockedUserId;
    }

    public UserId getBlockedByUserId() {
        return blockedByUserId;
    }

    public Optional<BlockReason> getReason() {
        return Optional.ofNullable(reason);
    }
}

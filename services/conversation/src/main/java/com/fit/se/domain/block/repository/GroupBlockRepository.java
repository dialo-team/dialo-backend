package com.fit.se.domain.block.repository;

import com.fit.se.domain.block.aggregate.GroupBlock;
import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupBlockRepository {
    Optional<GroupBlock> findByConversationIdAndBlockedUserId(ConversationId conversationId, UserId blockedUserId);

    List<GroupBlock> findAllByConversationId(ConversationId conversationId);

    GroupBlock save(GroupBlock groupBlock);

    void delete(GroupBlock groupBlock);
    Optional<GroupBlock> findByConversationIdAndBlockedUserId(UUID conversationId, Long blockedUserId);
    List<GroupBlock> findAllByConversationId(UUID conversationId);

}

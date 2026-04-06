package com.fit.se.infrastructure.persistence.mapper.block;

import com.fit.se.domain.block.model.GroupBlock;
import com.fit.se.infrastructure.persistence.entity.block.GroupBlockEntity;

public class BlockPersistenceMapper {

    public GroupBlockEntity toEntity(GroupBlock block) {
        GroupBlockEntity entity = new GroupBlockEntity();
        entity.setId(block.id());
        entity.setConversationId(block.conversationId());
        entity.setBlockedUserId(block.blockedUserId());
        entity.setBlockedBy(block.blockedBy());
        entity.setReason(block.reason());
        return entity;
    }

    public GroupBlock toDomain(GroupBlockEntity entity) {
        return new GroupBlock(
                entity.getId(),
                entity.getConversationId(),
                entity.getBlockedUserId(),
                entity.getBlockedBy(),
                entity.getReason()
        );
    }
}

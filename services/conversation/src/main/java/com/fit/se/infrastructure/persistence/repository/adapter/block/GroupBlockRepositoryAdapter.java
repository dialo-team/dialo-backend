package com.fit.se.infrastructure.persistence.repository.adapter.block;

import com.fit.se.domain.block.model.GroupBlock;
import com.fit.se.domain.block.repository.GroupBlockRepository;
import com.fit.se.infrastructure.persistence.mapper.block.BlockPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.block.SpringDataGroupBlockJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GroupBlockRepositoryAdapter implements GroupBlockRepository {

    private final SpringDataGroupBlockJpaRepository repository;
    private final BlockPersistenceMapper mapper = new BlockPersistenceMapper();

    public GroupBlockRepositoryAdapter(SpringDataGroupBlockJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public GroupBlock save(GroupBlock block) {
        return mapper.toDomain(repository.save(mapper.toEntity(block)));
    }

    @Override
    public Optional<GroupBlock> findByConversationIdAndBlockedUserId(UUID conversationId, Long blockedUserId) {
        return repository.findByConversationIdAndBlockedUserId(conversationId, blockedUserId).map(mapper::toDomain);
    }

    @Override
    public List<GroupBlock> findAllByConversationId(UUID conversationId) {
        return repository.findAllByConversationId(conversationId).stream().map(mapper::toDomain).toList();
    }
}

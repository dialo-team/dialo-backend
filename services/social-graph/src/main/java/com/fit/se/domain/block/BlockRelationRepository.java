package com.fit.se.domain.block;

import com.fit.se.domain.block.aggregate.BlockRelation;

import java.util.List;
import java.util.Optional;

public interface BlockRelationRepository {
    Optional<BlockRelation> findById(String id);
    Optional<BlockRelation> findActiveBetween(String userA, String userB);
    void save(BlockRelation blockRelation);

    void deleteBetween(String s, String s1);

    List<BlockRelation> findActiveByBlocker(String current);

    boolean existsActiveBlockBetween(String userAId, String userBId);
}
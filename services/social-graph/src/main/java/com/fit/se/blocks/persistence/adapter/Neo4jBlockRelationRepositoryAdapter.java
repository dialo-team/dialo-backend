package com.fit.se.blocks.persistence.adapter;

import com.fit.se.blocks.domain.BlockRelationRepository;
import com.fit.se.blocks.domain.aggregate.BlockRelation;
import com.fit.se.blocks.persistence.BlockPersistenceMapper;
import com.fit.se.blocks.persistence.relationship.BlockRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class Neo4jBlockRelationRepositoryAdapter implements BlockRelationRepository {

    private final Neo4jClient neo4jClient;

    @Override
    public Optional<BlockRelation> findById(String id) {
        String cypher = """
            MATCH (:User)-[r:BLOCK {blockId: $id}]->(:User)
            RETURN r
            LIMIT 1
            """;

        return neo4jClient.query(cypher)
                .bind(id).to("id")
                .fetchAs(BlockRelationship.class)
                .mappedBy((typeSystem, record) -> {
                    BlockRelationship r = new BlockRelationship();
                    r.setBlockId(record.get("r").get("blockId").asString());
                    r.setBlockerId(record.get("r").get("blockerId").asString());
                    r.setBlockedId(record.get("r").get("blockedId").asString());
                    r.setStatus(record.get("r").get("status").asString());
                    if (!record.get("r").get("createdAt").isNull()) {
                        r.setCreatedAt(record.get("r").get("createdAt").asZonedDateTime().toInstant());
                    }
                    if (!record.get("r").get("unblockedAt").isNull()) {
                        r.setUnblockedAt(record.get("r").get("unblockedAt").asZonedDateTime().toInstant());
                    }
                    if (!record.get("r").get("reason").isNull()) {
                        r.setReason(record.get("r").get("reason").asString());
                    }
                    if (!record.get("r").get("source").isNull()) {
                        r.setSource(record.get("r").get("source").asString());
                    }
                    return r;
                })
                .one()
                .map(BlockPersistenceMapper::toDomain);
    }

    @Override
    public Optional<BlockRelation> findActiveBetween(String userA, String userB) {
        String cypher = """
            MATCH (a:User {id: $userA})-[r:BLOCK]->(b:User {id: $userB})
            WHERE r.status = 'ACTIVE'
            RETURN r
            UNION
            MATCH (b:User {id: $userB})-[r:BLOCK]->(a:User {id: $userA})
            WHERE r.status = 'ACTIVE'
            RETURN r
            LIMIT 1
            """;

        return neo4jClient.query(cypher)
                .bind(userA).to("userA")
                .bind(userB).to("userB")
                .fetchAs(BlockRelationship.class)
                .mappedBy((typeSystem, record) -> {
                    BlockRelationship r = new BlockRelationship();
                    r.setBlockId(record.get("r").get("blockId").asString());
                    r.setBlockerId(record.get("r").get("blockerId").asString());
                    r.setBlockedId(record.get("r").get("blockedId").asString());
                    r.setStatus(record.get("r").get("status").asString());
                    if (!record.get("r").get("createdAt").isNull()) {
                        r.setCreatedAt(record.get("r").get("createdAt").asZonedDateTime().toInstant());
                    }
                    if (!record.get("r").get("unblockedAt").isNull()) {
                        r.setUnblockedAt(record.get("r").get("unblockedAt").asZonedDateTime().toInstant());
                    }
                    if (!record.get("r").get("reason").isNull()) {
                        r.setReason(record.get("r").get("reason").asString());
                    }
                    if (!record.get("r").get("source").isNull()) {
                        r.setSource(record.get("r").get("source").asString());
                    }
                    return r;
                })
                .one()
                .map(BlockPersistenceMapper::toDomain);
    }

    @Override
    public void save(BlockRelation blockRelation) {
        String cypher = """
            MATCH (blocker:User {id: $blockerId})
            MATCH (blocked:User {id: $blockedId})
            MERGE (blocker)-[r:BLOCK {blockId: $blockId}]->(blocked)
            SET r.blockerId = $blockerId,
                r.blockedId = $blockedId,
                r.status = $status,
                r.createdAt = $createdAt,
                r.unblockedAt = $unblockedAt,
                r.reason = $reason,
                r.source = $source
            """;

        neo4jClient.query(cypher)
                .bind(blockRelation.getId()).to("blockId")
                .bind(blockRelation.getBlockedId()).to("blockedId")
                .bind(blockRelation.getBlockerId()).to("blockerId")
                .bind(blockRelation.getStatus().name()).to("status")
                .bind(toOffsetDateTime(blockRelation.getCreatedAt())).to("createdAt")
                .bind(toOffsetDateTime(blockRelation.getUnblockedAt())).to("unblockedAt")
                .bind(blockRelation.getReason()).to("reason")
                .bind(blockRelation.getSource().name()).to("source")
                .run();
    }

    @Override
    public void deleteBetween(String blockerId, String blockedId) {
        String cypher = """
        MATCH (a:User {id: $blockerId})-[r:BLOCK]->(b:User {id: $blockedId})
        DELETE r
        """;

        neo4jClient.query(cypher)
                .bind(blockerId).to("blockerId")
                .bind(blockedId).to("blockedId")
                .run();
    }

    @Override
    public List<BlockRelation> findActiveByBlocker(String current) {
        String cypher = """
        MATCH (:User {id: $current})-[r:BLOCK]->(:User)
        WHERE r.status = 'ACTIVE'
        RETURN r
        """;

        return neo4jClient.query(cypher)
                .bind(current).to("current")
                .fetchAs(BlockRelationship.class)
                .mappedBy((typeSystem, record) -> {
                    BlockRelationship r = new BlockRelationship();
                    r.setBlockId(record.get("r").get("blockId").asString());
                    r.setBlockerId(record.get("r").get("blockerId").asString());
                    r.setBlockedId(record.get("r").get("blockedId").asString());
                    r.setStatus(record.get("r").get("status").asString());
                    if (!record.get("r").get("createdAt").isNull()) {
                        r.setCreatedAt(record.get("r").get("createdAt").asZonedDateTime().toInstant());
                    }
                    if (!record.get("r").get("unblockedAt").isNull()) {
                        r.setUnblockedAt(record.get("r").get("unblockedAt").asZonedDateTime().toInstant());
                    }
                    if (!record.get("r").get("reason").isNull()) {
                        r.setReason(record.get("r").get("reason").asString());
                    }
                    if (!record.get("r").get("source").isNull()) {
                        r.setSource(record.get("r").get("source").asString());
                    }
                    return r;
                })
                .all()
                .stream()
                .map(BlockPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsActiveBlockBetween(String userAId, String userBId) {
        String cypher = """
        MATCH (a:User {id: $userAId})-[r:BLOCK]-(b:User {id: $userBId})
        WHERE r.status = 'ACTIVE'
        RETURN count(r) > 0 AS exists
        """;

        Boolean result = neo4jClient.query(cypher)
                .bind(userAId).to("userAId")
                .bind(userBId).to("userBId")
                .fetchAs(Boolean.class)
                .mappedBy((typeSystem, record) -> record.get("exists").asBoolean())
                .one()
                .orElse(false);

        return result;
    }

    private OffsetDateTime toOffsetDateTime(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
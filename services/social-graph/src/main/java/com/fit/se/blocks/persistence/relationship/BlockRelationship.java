package com.fit.se.blocks.persistence.relationship;

import com.fit.se.user.persistence.node.UserNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

@RelationshipProperties
public class BlockRelationship {

    @Id
    @GeneratedValue
    private Long graphId;

    private String blockId;
    private String blockerId;
    private String blockedId;
    private String status;
    private Instant createdAt;
    private Instant unblockedAt;
    private String reason;
    private String source;

    @TargetNode
    private UserNode targetUser;

    public BlockRelationship() {
    }

    public BlockRelationship(UserNode targetUser) {
        this.targetUser = targetUser;
    }

    public Long getGraphId() {
        return graphId;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getBlockerId() {
        return blockerId;
    }

    public String getBlockedId() {
        return blockedId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUnblockedAt() {
        return unblockedAt;
    }

    public String getReason() {
        return reason;
    }

    public String getSource() {
        return source;
    }

    public UserNode getTargetUser() {
        return targetUser;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public void setBlockerId(String blockerId) {
        this.blockerId = blockerId;
    }

    public void setBlockedId(String blockedId) {
        this.blockedId = blockedId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUnblockedAt(Instant unblockedAt) {
        this.unblockedAt = unblockedAt;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTargetUser(UserNode targetUser) {
        this.targetUser = targetUser;
    }
}
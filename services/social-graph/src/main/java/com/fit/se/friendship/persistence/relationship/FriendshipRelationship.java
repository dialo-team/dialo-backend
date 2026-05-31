package com.fit.se.friendship.persistence.relationship;

import com.fit.se.user.persistence.node.UserNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

@RelationshipProperties
public class FriendshipRelationship {

    @Id
    @GeneratedValue
    private Long graphId;

    private String friendshipId;
    private String senderId;
    private String receiverId;
    private String status;
    private Instant requestedAt;
    private Instant respondedAt;
    private Instant acceptedAt;
    private Instant unfriendedAt;
    private String source;
    private String reason;

    @TargetNode
    private UserNode targetUser;

    public FriendshipRelationship() {
    }

    public FriendshipRelationship(UserNode targetUser) {
        this.targetUser = targetUser;
    }

    public Long getGraphId() { return graphId; }
    public String getFriendshipId() { return friendshipId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getRespondedAt() { return respondedAt; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public Instant getUnfriendedAt() { return unfriendedAt; }
    public String getSource() { return source; }
    public String getReason() { return reason; }
    public UserNode getTargetUser() { return targetUser; }
    public void setGraphId(Long graphId) { this.graphId = graphId; }
    public void setFriendshipId(String friendshipId) { this.friendshipId = friendshipId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setStatus(String status) { this.status = status; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public void setRespondedAt(Instant respondedAt) { this.respondedAt = respondedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }
    public void setUnfriendedAt(Instant unfriendedAt) { this.unfriendedAt = unfriendedAt; }
    public void setSource(String source) { this.source = source; }
    public void setReason(String reason) { this.reason = reason; }
    public void setTargetUser(UserNode targetUser) { this.targetUser = targetUser; }
}
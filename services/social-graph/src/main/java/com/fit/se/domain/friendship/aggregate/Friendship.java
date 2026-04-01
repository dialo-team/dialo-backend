package com.fit.se.domain.friendship.aggregate;

import com.fit.se.domain.friendship.valueobject.FriendshipSource;
import com.fit.se.domain.friendship.valueobject.FriendshipStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Friendship {
    private final String id;
    private final String senderId;
    private final String receiverId;

    private FriendshipStatus status;
    private final Instant requestedAt;
    private Instant respondedAt;
    private Instant acceptedAt;
    private Instant unfriendedAt;

    private FriendshipSource source;
    private String reason;

    public Friendship(
            String id,
            String senderId,
            String receiverId,
            Instant requestedAt
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Friendship id must not be blank");
        }
        if (senderId == null || senderId.isBlank()) {
            throw new IllegalArgumentException("Sender id must not be blank");
        }
        if (receiverId == null || receiverId.isBlank()) {
            throw new IllegalArgumentException("Receiver id must not be blank");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("A user cannot befriend themselves");
        }
        if (requestedAt == null) {
            throw new IllegalArgumentException("Requested time must not be null");
        }

        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = FriendshipStatus.PENDING;
        this.requestedAt = requestedAt;
    }

    public void accept(Instant at) {
        if (status != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Only pending friendship can be accepted");
        }
        if (at == null) {
            throw new IllegalArgumentException("Accept time must not be null");
        }

        this.status = FriendshipStatus.ACCEPTED;
        this.acceptedAt = at;
        this.respondedAt = at;
    }

    public void reject(Instant at) {
        if (status != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Only pending friendship can be rejected");
        }
        if (at == null) {
            throw new IllegalArgumentException("Reject time must not be null");
        }

        this.status = FriendshipStatus.REJECTED;
        this.respondedAt = at;
    }

    public void cancelRequest(Instant at) {
        if (status != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Only pending friendship can be cancelled");
        }
        if (at == null) {
            throw new IllegalArgumentException("Cancel time must not be null");
        }

        this.status = FriendshipStatus.CANCELLED;
        this.respondedAt = at;
    }

    public void unfriend(Instant at) {
        if (status != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted friendship can be unfriended");
        }
        if (at == null) {
            throw new IllegalArgumentException("Unfriend time must not be null");
        }

        this.status = FriendshipStatus.UNFRIENDED;
        this.unfriendedAt = at;
    }

    public boolean involves(String userId) {
        return senderId.equals(userId) || receiverId.equals(userId);
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public String otherOf(String currentUserId) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("Current user id must not be blank");
        }
        if (!involves(currentUserId)) {
            throw new IllegalArgumentException("User is not part of this friendship");
        }

        return senderId.equals(currentUserId) ? receiverId : senderId;
    }
}
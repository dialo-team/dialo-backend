package com.fit.se.application.friendship.command.accept;

import com.fit.se.domain.friendship.FriendshipRepository;
import com.fit.se.infrastructure.messaging.publisher.FriendEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcceptFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;
    private final FriendEventPublisher friendEventPublisher;

    public void execute(AcceptFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.senderId(), cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.isPending()) {
            throw new IllegalStateException("Only pending friend request can be accepted");
        }

        if (!friendship.getSenderId().equals(cmd.receiverId())) {
            throw new IllegalStateException("Invalid sender of friend request");
        }

        if (!friendship.getReceiverId().equals(cmd.senderId())) {
            throw new IllegalStateException("Only the receiver can accept this friend request");
        }

        friendship.accept(Instant.now());
        friendshipRepository.save(friendship);

        FriendAcceptedPayload payload = FriendAcceptedPayload.builder()
                .user1Id(cmd.senderId())
                .user2Id(cmd.receiverId())
                .systemMessage("Hai bạn đã trở thành bạn bè")
                .build();

        FriendAcceptedEvent event = FriendAcceptedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("friend.accepted")
                .occurredAt(Instant.now())
                .sourceService("social-service")
                .payload(payload)
                .build();

        friendEventPublisher.publishFriendAccepted(event);
    }
}
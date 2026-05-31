package com.fit.se.friendship.application.command.accept;

import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.event.producer.FriendEventPublisher;
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
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay loi moi ket ban"));

        if (!friendship.isPending()) {
            throw new IllegalStateException("Chi co the chap nhan loi moi ket ban dang cho phan hoi");
        }

        if (!friendship.getSenderId().equals(cmd.receiverId())) {
            throw new IllegalStateException("Nguoi gui loi moi khong hop le");
        }

        if (!friendship.getReceiverId().equals(cmd.senderId())) {
            throw new IllegalStateException("Chi nguoi nhan moi co the chap nhan loi moi nay");
        }

        friendship.accept(Instant.now());
        friendshipRepository.save(friendship);

        FriendAcceptedPayload payload = FriendAcceptedPayload.builder()
                .user1Id(cmd.senderId())
                .user2Id(cmd.receiverId())
                .systemMessage("Hai ban da tro thanh ban be")
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
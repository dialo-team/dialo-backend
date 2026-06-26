package com.fit.se.friendship.application.command.request;

import com.fit.se.blocks.domain.BlockRelationRepository;
import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.friendship.domain.aggregate.Friendship;
import com.fit.se.friendship.domain.valueobject.FriendshipSource;
import com.fit.se.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RequestFriendCommandHandler {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockRelationRepository blockRelationRepository;

    public void execute(RequestFriendCommand cmd) {
        var sender = userRepository.findById(cmd.senderId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi gui loi moi"));

        var receiver = userRepository.findById(cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi nhan loi moi"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Ban khong the gui loi moi ket ban cho chinh minh");
        }

        if (blockRelationRepository.findActiveBetween(cmd.senderId(), cmd.receiverId()).isPresent()) {
            throw new IllegalStateException("Khong the gui loi moi ket ban khi dang co chan giua hai ben");
        }

        if (friendshipRepository.existsActiveOrPendingBetween(cmd.senderId(), cmd.receiverId())) {
            throw new IllegalStateException("Hai ben da la ban be hoac dang co loi moi ket ban");
        }

        FriendshipSource source = parseSource(cmd.source());
        var friendship = new Friendship(
                cmd.friendshipId(),
                cmd.senderId(),
                cmd.receiverId(),
                Instant.now(),
                source,
                normalizeReason(cmd.reason())
        );

        friendshipRepository.save(friendship);
    }

    private FriendshipSource parseSource(String source) {
        if (source == null || source.isBlank()) {
            return FriendshipSource.DIRECT;
        }
        return FriendshipSource.valueOf(source);
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return null;
        }
        String trimmed = reason.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
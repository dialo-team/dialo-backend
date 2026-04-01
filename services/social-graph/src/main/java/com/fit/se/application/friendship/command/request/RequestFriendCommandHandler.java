package com.fit.se.application.friendship.command.request;

import com.fit.se.domain.block.BlockRelationRepository;
import com.fit.se.domain.friendship.FriendshipRepository;
import com.fit.se.domain.friendship.aggregate.Friendship;
import com.fit.se.domain.user.UserRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        var receiver = userRepository.findById(cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("A user cannot send friend request to themselves");
        }

        if (blockRelationRepository.findActiveBetween(cmd.senderId(), cmd.receiverId()).isPresent()) {
            throw new IllegalStateException("Cannot send friend request while block exists");
        }

        if (friendshipRepository.existsActiveOrPendingBetween(cmd.senderId(), cmd.receiverId())) {
            throw new IllegalStateException("Friendship already active or pending");
        }

        var friendship = new Friendship(
                cmd.friendshipId(),
                cmd.senderId(),
                cmd.receiverId(),
                Instant.now()
        );

        friendshipRepository.save(friendship);
    }
}
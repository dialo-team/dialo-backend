package com.fit.se.application.friendship.command.unfriend;

import com.fit.se.domain.friendship.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;

    public void execute(UnFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.userAId(), cmd.userBId())
                .orElseThrow(() -> new IllegalArgumentException("Accepted friendship not found"));

        if (!friendship.isAccepted()) {
            throw new IllegalStateException("Only accepted friendship can be unfriended");
        }

        friendshipRepository.deleteBetween(cmd.userAId(), cmd.userBId());
    }
}
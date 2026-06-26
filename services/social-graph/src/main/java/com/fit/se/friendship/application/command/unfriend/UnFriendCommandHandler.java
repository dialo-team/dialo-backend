package com.fit.se.friendship.application.command.unfriend;

import com.fit.se.friendship.domain.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;

    public void execute(UnFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.userAId(), cmd.userBId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay quan he ban be"));

        if (!friendship.isAccepted()) {
            throw new IllegalStateException("Chi co the xoa quan he khi hai ben da la ban be");
        }

        friendshipRepository.deleteBetween(cmd.userAId(), cmd.userBId());
    }
}
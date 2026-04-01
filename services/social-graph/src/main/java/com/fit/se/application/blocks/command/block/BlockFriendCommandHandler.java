package com.fit.se.application.blocks.command.block;

import com.fit.se.domain.block.BlockRelationRepository;
import com.fit.se.domain.block.aggregate.BlockRelation;
import com.fit.se.domain.block.valueobject.BlockSource;
import com.fit.se.domain.friendship.FriendshipRepository;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlockFriendCommandHandler {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockRelationRepository blockRelationRepository;

    public void execute(BlockFriendCommand cmd) {
        if (cmd.blockerId().equals(cmd.blockedId())) {
            throw new IllegalArgumentException("A user cannot block themselves");
        }

        userRepository.findById(cmd.blockerId())
                .orElseThrow(() -> new IllegalArgumentException("Blocker not found"));

        userRepository.findById(cmd.blockedId())
                .orElseThrow(() -> new IllegalArgumentException("Blocked user not found"));

        if (blockRelationRepository.findActiveBetween(cmd.blockerId(), cmd.blockedId()).isPresent()) {
            throw new IllegalStateException("Block relation already exists");
        }

        friendshipRepository.findBetween(cmd.blockerId(), cmd.blockedId())
                .filter(it -> it.isAccepted())
                .ifPresent(it -> friendshipRepository.deleteBetween(cmd.blockerId(), cmd.blockedId()));

        var blockRelation = new BlockRelation(
                cmd.blockId(),
                cmd.blockerId(),
                cmd.blockedId(),
                Instant.now(),
                cmd.reason(),
                BlockSource.USER_ACTION
        );

        blockRelationRepository.save(blockRelation);
    }
}
package com.fit.se.blocks.application.command.unblock;

import com.fit.se.blocks.domain.BlockRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnblockFriendCommandHandler {

    private final BlockRelationRepository blockRelationRepository;

    public void execute(UnblockFriendCommand cmd) {
        var blockRelation = blockRelationRepository.findActiveBetween(cmd.blockerId(), cmd.blockedId())
                .orElseThrow(() -> new IllegalArgumentException("Active block relation not found"));

        if (!blockRelation.getBlockerId().equals(cmd.blockerId())) {
            throw new IllegalStateException("Only blocker can unblock");
        }

        if (!blockRelation.getBlockedId().equals(cmd.blockedId())) {
            throw new IllegalStateException("Invalid blocked user");
        }

        blockRelationRepository.deleteBetween(cmd.blockerId(), cmd.blockedId());
    }
}
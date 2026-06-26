package com.fit.se.blocks.controller;

import com.fit.se.blocks.dto.request.BlockRequest;
import com.fit.se.blocks.application.command.block.BlockFriendCommand;
import com.fit.se.blocks.application.command.block.BlockFriendCommandHandler;
import com.fit.se.blocks.application.command.unblock.UnblockFriendCommand;
import com.fit.se.blocks.application.command.unblock.UnblockFriendCommandHandler;
import com.fit.se.common.context.HolderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class BlockController {
    private final BlockFriendCommandHandler blockFriendHandler;
    private final UnblockFriendCommandHandler unblockFriendHandler;

    @PostMapping("/{targetUser}/block")
    public void block(@PathVariable("targetUser") String target,
                      @RequestBody(required = false) BlockRequest request) {
        BlockFriendCommand cmd = BlockFriendCommand.builder()
                .blockerId(HolderContext.getRequiredUserId())
                .blockedId(target)
                .blockId(UUID.randomUUID().toString())
                .build();
        blockFriendHandler.execute(cmd);
    }

    @DeleteMapping("/{targetUser}/unblock")
    public void unBlock(@PathVariable("targetUser") String target) {
        UnblockFriendCommand cmd = UnblockFriendCommand.builder()
                .blockerId(HolderContext.getRequiredUserId())
                .blockedId(target)
                .build();
        unblockFriendHandler.execute(cmd);
    }
}

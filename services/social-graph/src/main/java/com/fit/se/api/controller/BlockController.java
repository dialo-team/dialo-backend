package com.fit.se.api.controller;

import com.fit.se.api.dto.request.BlockRequest;
import com.fit.se.application.blocks.command.block.BlockFriendCommand;
import com.fit.se.application.blocks.command.block.BlockFriendCommandHandler;
import com.fit.se.application.blocks.command.unblock.UnblockFriendCommand;
import com.fit.se.application.blocks.command.unblock.UnblockFriendCommandHandler;
import com.fit.se.infrastructure.config.context.HolderContext;
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

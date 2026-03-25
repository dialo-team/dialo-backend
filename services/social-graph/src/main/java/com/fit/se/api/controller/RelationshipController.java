package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.application.relationship.command.accept.AcceptCommandHandler;
import com.fit.se.application.relationship.command.block.BlockCommandHandler;
import com.fit.se.application.relationship.command.cancel.CancelCommandHandler;
import com.fit.se.application.relationship.command.request.SendFriendCommandHandler;
import com.fit.se.application.relationship.command.unblock.UnblockCommandHandler;
import com.fit.se.application.relationship.command.unfriend.UnfriendCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class RelationshipController {
    private final SendFriendCommandHandler sendFriendCommandHandler;
    private final AcceptCommandHandler acceptCommandHandler;
    private final BlockCommandHandler blockCommandHandler;
    private final UnblockCommandHandler unblockCommandHandler;
    private final UnfriendCommandHandler unfriendCommandHandler;
    private final CancelCommandHandler cancelCommandHandler;

    @PostMapping("/request")
    public void sendRequest(@RequestBody SendFriendRequest request) {
        sendFriendCommandHandler.execute(request.from(), request.to());
    }

    @PostMapping("/accept")
    public void accept(@RequestBody AcceptFriendRequest request) {
        acceptCommandHandler.execute(request.from(), request.to());
    }

    @PostMapping("/block")
    public void block(@RequestBody BlockRequest request) {
        blockCommandHandler.execute(request.from(), request.to());
    }

    @PostMapping("/unblock")
    public void unBlock(@RequestBody UnblockRequest request) {
        unblockCommandHandler.execute(request.from(), request.to());
    }

    @PostMapping("/unfriend")
    public void unFriend(@RequestBody UnfriendRequest request) {
        unfriendCommandHandler.execute(request.from(), request.to());
    }

    @PostMapping("/cancel-request")
    public void cancelRequest(@RequestBody CancelRequest request) {
        cancelCommandHandler.execute(request.from(), request.to());
    }
}

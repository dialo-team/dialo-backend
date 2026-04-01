package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.application.friendship.command.accept.AcceptFriendCommand;
import com.fit.se.application.friendship.command.accept.AcceptFriendCommandHandler;
import com.fit.se.application.friendship.command.cancel.CancelFriendCommand;
import com.fit.se.application.friendship.command.cancel.CancelFriendCommandHandler;
import com.fit.se.application.friendship.command.reject.RejectFriendCommand;
import com.fit.se.application.friendship.command.reject.RejectFriendCommandHandler;
import com.fit.se.application.friendship.command.request.RequestFriendCommand;
import com.fit.se.application.friendship.command.request.RequestFriendCommandHandler;
import com.fit.se.application.friendship.command.unfriend.UnFriendCommand;
import com.fit.se.application.friendship.command.unfriend.UnFriendCommandHandler;
import com.fit.se.application.friendship.query.check.CheckFriendQuery;
import com.fit.se.application.friendship.query.check.CheckFriendQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendController {
    private final RequestFriendCommandHandler requestFriendHandler;
    private final AcceptFriendCommandHandler acceptFriendHandler;
    private final RejectFriendCommandHandler rejectFriendHandler;
    private final UnFriendCommandHandler unFriendHandler;
    private final CheckFriendQueryHandler checkFriendHandler;
    private final CancelFriendCommandHandler cancelFriendHandler;


    @PostMapping("/{targetId}/request")
    public void sendRequest(@PathVariable("targetId") String receiverId,
                            @RequestHeader("X-User-Id") String senderId,
                            @RequestBody(required = false) SendFriendRequest request) {
        RequestFriendCommand cmd = RequestFriendCommand.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .friendshipId(UUID.randomUUID().toString())
                .build();
        requestFriendHandler.execute(cmd);
    }

    @DeleteMapping("/{targetId}/request")
    public void cancelRequest(@PathVariable("targetId") String receiverId,
                              @RequestHeader("X-User-Id") String senderId) {
        CancelFriendCommand cmd = CancelFriendCommand.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        cancelFriendHandler.execute(cmd);
    }

    @PostMapping("/{targetId}/accept")
    public void accept(@PathVariable("targetId") String receiverId,
                       @RequestHeader("X-User-Id") String senderId) {
        AcceptFriendCommand cmd = AcceptFriendCommand.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        acceptFriendHandler.execute(cmd);
    }

    @PostMapping("/{targetId}/reject")
    public void rejectRequest(@PathVariable("targetId") String receiverId,
                              @RequestHeader("X-User-Id") String senderId) {
        RejectFriendCommand cmd = RejectFriendCommand.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        rejectFriendHandler.execute(cmd);
    }

    @DeleteMapping("/{targetId}/unfriend")
    public void unFriend(@PathVariable("targetId") String receiverId,
                         @RequestHeader("X-User-Id") String senderId) {
        UnFriendCommand cmd = UnFriendCommand.builder()
                .userAId(senderId)
                .userBId(receiverId)
                .build();
        unFriendHandler.execute(cmd);
    }

    @GetMapping("/{targetId}/check")
    public void check(@PathVariable("targetId") String receiverId,
                      @RequestHeader("X-User-Id") String senderId) {
        CheckFriendQuery query = CheckFriendQuery.builder()
                .build();
        checkFriendHandler.execute(query);
    }

}

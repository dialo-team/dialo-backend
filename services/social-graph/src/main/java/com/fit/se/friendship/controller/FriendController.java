package com.fit.se.friendship.controller;

import com.fit.se.friendship.application.command.accept.AcceptFriendCommand;
import com.fit.se.friendship.application.command.accept.AcceptFriendCommandHandler;
import com.fit.se.friendship.application.command.cancel.CancelFriendCommand;
import com.fit.se.friendship.application.command.cancel.CancelFriendCommandHandler;
import com.fit.se.friendship.application.command.reject.RejectFriendCommand;
import com.fit.se.friendship.application.command.reject.RejectFriendCommandHandler;
import com.fit.se.friendship.application.command.request.RequestFriendCommand;
import com.fit.se.friendship.application.command.request.RequestFriendCommandHandler;
import com.fit.se.friendship.application.command.unfriend.UnFriendCommand;
import com.fit.se.friendship.application.command.unfriend.UnFriendCommandHandler;
import com.fit.se.friendship.application.query.check.CheckFriendQuery;
import com.fit.se.friendship.application.query.check.CheckFriendQueryHandler;
import com.fit.se.friendship.dto.request.SendFriendRequest;
import com.fit.se.common.dto.response.ApiResponse;
import com.fit.se.common.context.HolderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                            @RequestBody(required = false) SendFriendRequest request) {
        RequestFriendCommand cmd = RequestFriendCommand.builder()
                .senderId(HolderContext.getRequiredUserId())
                .receiverId(receiverId)
                .friendshipId(UUID.randomUUID().toString())
                .reason(request == null ? null : request.reason())
                .source("DIRECT")
                .build();
        requestFriendHandler.execute(cmd);
    }

    @DeleteMapping("/{targetId}/request")
    public void cancelRequest(@PathVariable("targetId") String receiverId) {
        CancelFriendCommand cmd = CancelFriendCommand.builder()
                .senderId(HolderContext.getRequiredUserId())
                .receiverId(receiverId)
                .build();
        cancelFriendHandler.execute(cmd);
    }

    @PostMapping("/{targetId}/accept")
    public void accept(@PathVariable("targetId") String receiverId) {
        AcceptFriendCommand cmd = AcceptFriendCommand.builder()
                .senderId(HolderContext.getRequiredUserId())
                .receiverId(receiverId)
                .build();
        acceptFriendHandler.execute(cmd);
    }

    @PostMapping("/{targetId}/reject")
    public void rejectRequest(@PathVariable("targetId") String receiverId) {
        RejectFriendCommand cmd = RejectFriendCommand.builder()
                .senderId(HolderContext.getRequiredUserId())
                .receiverId(receiverId)
                .build();
        rejectFriendHandler.execute(cmd);
    }

    @DeleteMapping("/{targetId}/unfriend")
    public void unFriend(@PathVariable("targetId") String receiverId) {
        UnFriendCommand cmd = UnFriendCommand.builder()
                .userAId(HolderContext.getRequiredUserId())
                .userBId(receiverId)
                .build();
        unFriendHandler.execute(cmd);
    }

    @GetMapping("/{targetId}/check")
    public ApiResponse<?> check(@PathVariable("targetId") String receiverId) {
        CheckFriendQuery query = CheckFriendQuery.builder()
                .currentUserId(HolderContext.getRequiredUserId())
                .targetUserId(receiverId)
                .build();
        return ApiResponse.builder()
                .data(checkFriendHandler.execute(query))
                .build();
    }
}
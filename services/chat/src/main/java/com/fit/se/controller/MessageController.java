package com.fit.se.controller;

import com.fit.se.dto.DeleteMessageRequest;
import com.fit.se.dto.AddPollOptionRequest;
import com.fit.se.dto.CreatePollRequest;
import com.fit.se.dto.EditMessageRequest;
import com.fit.se.dto.ForwardMessageRequest;
import com.fit.se.dto.MessageReactionRequest;
import com.fit.se.dto.MessageResponse;
import com.fit.se.dto.SendMessageRequest;
import com.fit.se.dto.VotePollRequest;
import com.fit.se.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public MessageResponse sendMessage(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        request.setSenderId(userId);
        return messageService.sendMessage(request);
    }

    @PostMapping("/poll")
    public MessageResponse createPoll(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePollRequest request
    ) {
        return messageService.createPoll(userId, request);
    }

    @PostMapping("/file")
    public MessageResponse sendFile(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String conversationId,
            @RequestParam MultipartFile file
    ) {
        return messageService.sendFile(conversationId, userId, file);
    }

    @PostMapping("/forward")
    public MessageResponse forward(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ForwardMessageRequest request
    ) {
        request.setSenderId(userId);
        return messageService.forwardMessage(request);
    }

    @PostMapping("/{messageId}/revoke")
    public MessageResponse revoke(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return messageService.revokeMessage(messageId, userId);
    }

    @PutMapping("/{messageId}")
    public MessageResponse editMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody EditMessageRequest request
    ) {
        return messageService.editMessage(messageId, userId, request);
    }

    @PostMapping("/{messageId}/react")
    public MessageResponse reactMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody MessageReactionRequest request
    ) {
        return messageService.reactToMessage(messageId, userId, request);
    }

    @PutMapping("/{messageId}/poll/votes")
    public MessageResponse votePoll(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody VotePollRequest request
    ) {
        return messageService.votePoll(messageId, userId, request);
    }

    @PostMapping("/{messageId}/poll/options")
    public MessageResponse addPollOption(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddPollOptionRequest request
    ) {
        return messageService.addPollOption(messageId, userId, request);
    }

    @PostMapping("/{messageId}/poll/close")
    public MessageResponse closePoll(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return messageService.closePoll(messageId, userId);
    }

    @DeleteMapping("/{messageId}")
    public MessageResponse deleteForMe(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setUserId(userId);
        return messageService.deleteForMe(messageId, request);
    }
}

package com.fit.se.message.controller;

import com.fit.se.message.dto.DeleteMessageResponse;
import com.fit.se.message.dto.EditMessageRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.message.dto.ForwardMessageRequest;
import com.fit.se.message.dto.ReactionRequest;
import com.fit.se.message.dto.SendDirectMessageRequest;
import com.fit.se.message.dto.SendMessageRequest;
import com.fit.se.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public MessageResponse createMessage(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        return messageService.sendMessage(userId, request);
    }

    @PostMapping("/direct")
    public MessageResponse createDirectMessage(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SendDirectMessageRequest request
    ) {
        return messageService.sendDirectMessage(userId, request);
    }

    @PostMapping("/{messageId}/reactions")
    public MessageResponse addReaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId,
            @Valid @RequestBody ReactionRequest request
    ) {
        return messageService.addReaction(userId, messageId, request.getEmoji());
    }

    @DeleteMapping("/{messageId}/reactions/{emoji}")
    public MessageResponse removeReaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId,
            @PathVariable int emoji
    ) {
        return messageService.removeReaction(userId, messageId, emoji);
    }

    @PutMapping("/{messageId}/pin")
    public MessageResponse pinMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId
    ) {
        return messageService.pinMessage(userId, messageId);
    }

    @DeleteMapping("/{messageId}/pin")
    public MessageResponse unpinMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId
    ) {
        return messageService.unpinMessage(userId, messageId);
    }

    @PutMapping("/{messageId}/revoke")
    public MessageResponse revokeMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId
    ) {
        return messageService.revokeMessage(userId, messageId);
    }

    @PostMapping("/{messageId}/forward")
    public MessageResponse forwardMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId,
            @Valid @RequestBody ForwardMessageRequest request
    ) {
        return messageService.forwardMessage(userId, messageId, request);
    }

    @DeleteMapping("/{messageId}")
    public DeleteMessageResponse deleteMessageForMe(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId
    ) {
        return messageService.deleteMessageForMe(userId, messageId);
    }

    @DeleteMapping("/{messageId}/everyone")
    public MessageResponse deleteMessageForEveryone(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId
    ) {
        return messageService.deleteMessageForEveryone(userId, messageId);
    }

    @PatchMapping("/{messageId}")
    public MessageResponse editMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId,
            @Valid @RequestBody EditMessageRequest request
    ) {
        return messageService.editMessage(userId, messageId, request);
    }
}

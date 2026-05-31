package com.fit.se.call.controller;

import com.fit.se.call.dto.CreateCallRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.call.service.CallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class CallController {
    private final CallService callService;

    @PostMapping("/{conversationId}/calls")
    public MessageResponse createCall(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateCallRequest request
    ) {
        return callService.createCall(conversationId, userId, request);
    }

    @PostMapping("/{conversationId}/calls/{messageId}/end")
    public MessageResponse endCall(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return callService.endCall(conversationId, messageId, userId);
    }
}

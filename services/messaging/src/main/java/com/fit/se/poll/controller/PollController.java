package com.fit.se.poll.controller;

import com.fit.se.poll.dto.AddPollOptionRequest;
import com.fit.se.poll.dto.CreatePollRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.poll.dto.PollAnswerVotesResponse;
import com.fit.se.poll.dto.VotePollRequest;
import com.fit.se.poll.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class PollController {
    private final PollService pollService;

    @PostMapping("/{conversationId}/polls")
    public MessageResponse createPoll(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePollRequest request
    ) {
        return pollService.createPoll(conversationId, userId, request);
    }

    @PostMapping("/{conversationId}/polls/{messageId}/votes")
    public MessageResponse votePoll(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody VotePollRequest request
    ) {
        return pollService.vote(conversationId, messageId, userId, request);
    }

    @GetMapping("/{conversationId}/polls/{messageId}/answers/{answerId}")
    public PollAnswerVotesResponse getAnswerVotes(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @PathVariable int answerId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return pollService.getAnswerVotes(conversationId, messageId, answerId, userId);
    }

    @PostMapping("/{conversationId}/polls/{messageId}/close")
    public MessageResponse closePoll(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return pollService.closePoll(conversationId, messageId, userId);
    }

    @PostMapping("/{conversationId}/polls/{messageId}/options")
    public MessageResponse addOption(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddPollOptionRequest request
    ) {
        return pollService.addOption(conversationId, messageId, userId, request);
    }

    @DeleteMapping("/{conversationId}/polls/{messageId}")
    public MessageResponse deletePoll(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return pollService.deletePoll(conversationId, messageId, userId);
    }
}

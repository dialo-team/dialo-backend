package com.fit.se.conversation.controller;

import com.fit.se.conversation.dto.ConversationDetailResponse;
import com.fit.se.conversation.dto.ConversationSummaryResponse;
import com.fit.se.conversation.dto.AddGroupMembersRequest;
import com.fit.se.conversation.dto.CreateConversationRequest;
import com.fit.se.conversation.dto.CreateGroupConversationRequest;
import com.fit.se.conversation.dto.DissolveGroupResponse;
import com.fit.se.conversation.dto.LeaveGroupResponse;
import com.fit.se.conversation.dto.RemoveGroupMemberResponse;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleRequest;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleResponse;
import com.fit.se.conversation.dto.TypingIndicatorRequest;
import com.fit.se.conversation.dto.TransferGroupOwnerRequest;
import com.fit.se.conversation.dto.TransferGroupOwnerResponse;
import com.fit.se.conversation.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationSummaryResponse> listConversations(@RequestHeader("X-User-Id") String userId) {
        return conversationService.getUserConversations(userId);
    }

    @GetMapping("/{conversationId}")
    public ConversationDetailResponse getConversation(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getConversationDetail(conversationId, userId);
    }

    @PostMapping
    public ConversationSummaryResponse createConversation(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateConversationRequest request
    ) {
        return conversationService.createDirectConversation(userId, request);
    }

    @PostMapping("/groups")
    public ConversationSummaryResponse createGroupConversation(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateGroupConversationRequest request
    ) {
        return conversationService.createGroupConversation(userId, request);
    }

    @PostMapping("/{conversationId}/members")
    public ConversationSummaryResponse addGroupMembers(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddGroupMembersRequest request
    ) {
        return conversationService.addGroupMembers(conversationId, userId, request);
    }

    @DeleteMapping("/{conversationId}/members/{memberUserId}")
    public RemoveGroupMemberResponse removeGroupMember(
            @PathVariable String conversationId,
            @PathVariable String memberUserId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.removeGroupMember(conversationId, userId, memberUserId);
    }

    @PutMapping("/{conversationId}/members/{memberUserId}/role")
    public UpdateGroupMemberRoleResponse updateGroupMemberRole(
            @PathVariable String conversationId,
            @PathVariable String memberUserId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateGroupMemberRoleRequest request
    ) {
        return conversationService.updateGroupMemberRole(conversationId, userId, memberUserId, request);
    }

    @PutMapping("/{conversationId}/owner")
    public TransferGroupOwnerResponse transferGroupOwner(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody TransferGroupOwnerRequest request
    ) {
        return conversationService.transferGroupOwner(conversationId, userId, request);
    }

    @PostMapping("/{conversationId}/leave")
    public LeaveGroupResponse leaveGroup(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.leaveGroup(conversationId, userId);
    }

    @DeleteMapping("/{conversationId}")
    public DissolveGroupResponse dissolveGroup(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.dissolveGroup(conversationId, userId);
    }

    @PutMapping("/{conversationId}/read")
    public ConversationSummaryResponse markRead(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.markRead(conversationId, userId);
    }

    @PostMapping("/{conversationId}/typing")
    public void typing(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TypingIndicatorRequest request
    ) {
        conversationService.sendTypingIndicator(conversationId, userId, request.isTyping());
    }
}

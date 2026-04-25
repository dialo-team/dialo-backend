package com.fit.se.controller;

import com.fit.se.dto.*;
import com.fit.se.entity.Conversation;
import com.fit.se.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping("/{conversationId}/members")
    public List<GroupMemberResponse> getGroupMembers(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getGroupMembers(conversationId, userId);
    }

    @PutMapping("/{conversationId}/group-name")
    public Conversation updateGroupName(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateGroupNameRequest request
    ) {
        return conversationService.updateGroupName(conversationId, userId, request);
    }

    @PutMapping("/{conversationId}/group-avatar")
    public Conversation updateGroupAvatar(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateGroupAvatarRequest request
    ) {
        return conversationService.updateGroupAvatar(conversationId, userId, request);
    }

    @PutMapping(value = "/{conversationId}/group-avatar", consumes = "multipart/form-data")
    public Conversation updateGroupAvatarFile(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam MultipartFile file
    ) {
        return conversationService.updateGroupAvatar(conversationId, userId, file);
    }

    @PutMapping("/{conversationId}/group-description")
    public Conversation updateGroupDescription(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateGroupDescriptionRequest request
    ) {
        return conversationService.updateGroupDescription(conversationId, userId, request);
    }

    @PostMapping("/{conversationId}/leave")
    public Conversation leaveGroup(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.leaveGroup(conversationId, userId);
    }

    @PostMapping("/groups")
    public Conversation createGroup(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateGroupRequest request
    ) {
        return conversationService.createGroupConversation(userId, request);
    }

    @PostMapping("/{conversationId}/members")
    public Conversation addMembers(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateMembersRequest request
    ) {
        return conversationService.addMembers(conversationId, userId, request);
    }

    @PutMapping("/{conversationId}/members/{memberId}/nickname")
    public Conversation updateMemberNickname(
            @PathVariable String conversationId,
            @PathVariable String memberId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateMemberNicknameRequest request
    ) {
        return conversationService.updateMemberNickname(conversationId, userId, memberId, request);
    }

    @DeleteMapping("/{conversationId}/members/{memberId}")
    public Conversation removeMember(
            @PathVariable String conversationId,
            @PathVariable String memberId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.removeMember(conversationId, userId, memberId);
    }

    @PutMapping("/{conversationId}/members/{memberId}/role")
    public Conversation assignRole(
            @PathVariable String conversationId,
            @PathVariable String memberId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AssignRoleRequest request
    ) {
        return conversationService.assignRole(conversationId, userId, memberId, request);
    }

    @DeleteMapping("/{conversationId}/dissolve")
    public void dissolveGroup(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        conversationService.dissolveGroup(conversationId, userId);
    }

    @PostMapping
    public Conversation createConversation(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateConversationRequest request
    ) {
        request.setCreatedBy(userId);
        return conversationService.createConversation(request);
    }

    @GetMapping
    public List<ConversationSummaryResponse> getUserConversations(
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getUserConversations(userId);
    }

    @GetMapping("/{conversationId}")
    public ConversationDetailResponse getConversationDetail(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getConversationDetail(conversationId, userId);
    }

    @PutMapping("/{conversationId}/remark")
    public ConversationSummaryResponse updateRemark(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateRemarkRequest request
    ) {
        request.setRequesterId(userId);
        return conversationService.updateRemark(conversationId, request);
    }

    @PutMapping("/{conversationId}/read")
    public ConversationSummaryResponse markRead(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        MarkReadRequest request = new MarkReadRequest();
        request.setUserId(userId);
        return conversationService.markRead(conversationId, request);
    }

    @PostMapping("/{conversationId}/clear-history")
    public ConversationSummaryResponse clearHistory(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        ClearHistoryRequest request = new ClearHistoryRequest();
        request.setUserId(userId);
        return conversationService.clearHistory(conversationId, request);
    }

    @GetMapping("/{conversationId}/media")
    public List<MessageResponse> getMedia(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getMedia(conversationId, userId);
    }

    @GetMapping("/{conversationId}/search")
    public List<MessageResponse> search(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String keyword
    ) {
        return conversationService.searchMessages(conversationId, userId, keyword);
    }

    @GetMapping("/{conversationId}/members/search")
    public List<GroupMemberResponse> searchMembers(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String keyword
    ) {
        return conversationService.searchGroupMembers(conversationId, userId, keyword);
    }

    @GetMapping("/{conversationId}/history")
    public List<GroupMemberHistoryResponse> getGroupHistory(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getGroupHistory(conversationId, userId);
    }

    @PostMapping("/{conversationId}/typing")
    public void typing(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TypingIndicatorRequest request
    ) {
        conversationService.sendTypingIndicator(conversationId, userId, request.isTyping());
    }

    @PostMapping("/{conversationId}/pin/{messageId}")
    public void pinMessage(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        conversationService.pinMessage(conversationId, messageId, userId);
    }

    @DeleteMapping("/{conversationId}/pin/{messageId}")
    public void unpinMessage(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId
    ) {
        conversationService.unpinMessage(conversationId, messageId, userId);
    }

    @GetMapping("/{conversationId}/pins")
    public List<PinnedMessageResponse> getPinnedMessages(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return conversationService.getPinnedMessages(conversationId, userId);
    }
}

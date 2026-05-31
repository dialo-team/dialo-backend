package com.fit.se.call.service;

import com.fit.se.call.dto.CreateCallRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.message.dto.SendMessageRequest;
import com.fit.se.user.domain.User;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.message.domain.Message;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.message.service.MessageService;
import com.fit.se.user.service.UserDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserDirectoryService userDirectoryService;

    public MessageResponse createCall(String conversationId, String userId, CreateCallRequest request) {
        if (request == null || request.getType() == null || request.getType().isBlank()) {
            throw new IllegalArgumentException("Loai cuoc goi khong hop le");
        }

        Conversation conversation = conversationService.getConversationEntity(conversationId);
        conversationService.getActiveMembership(conversationId, userId);

        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setConversationId(conversationId);
        CallInfo call = new CallInfo();
        call.setType(request.getType().trim().toUpperCase());
        call.setStatus("ONGOING");
        call.setStartedTime(Instant.now());
        call.setUsers(resolveCallUsers(conversationId, userId, request.getUserIds()));
        sendMessageRequest.setCall(call);
        sendMessageRequest.setContent("");
        return messageService.sendMessage(userId, sendMessageRequest);
    }

    public MessageResponse endCall(String conversationId, String messageId, String userId) {
        conversationService.getActiveMembership(conversationId, userId);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay message"));
        if (!conversationId.equals(message.getConversationId())) {
            throw new ResourceNotFoundException("Khong tim thay call trong conversation");
        }
        if (message.getCall() == null) {
            throw new IllegalArgumentException("Message nay khong phai call");
        }
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the thao tac tren call da bi thu hoi/xoa");
        }
        if ("ENDED".equalsIgnoreCase(message.getCall().getStatus())) {
            return conversationService.toMessageResponse(message, userId);
        }

        message.getCall().setStatus("ENDED");
        message.getCall().setEndedTime(Instant.now());
        Message saved = messageRepository.save(message);
        conversationService.publishMessageUpdateEvent(saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    private List<User> resolveCallUsers(String conversationId, String requesterId, List<String> requestedUserIds) {
        List<String> userIds = requestedUserIds == null ? List.of() : requestedUserIds.stream()
                .filter(item -> item != null && !item.isBlank())
                .distinct()
                .toList();

        List<User> users = new ArrayList<>();
        if (userIds.isEmpty()) {
            users.add(new User(requesterId, userDirectoryService.getRequired(requesterId).getDisplayName(), userDirectoryService.getRequired(requesterId).getAvatarUrl()));
            for (var member : conversationService.getActiveMembers(conversationId)) {
                if (requesterId.equals(member.getUserId())) {
                    continue;
                }
                users.add(new User(member.getUserId(), conversationService.resolveMemberDisplayName(member), member.getAvatarUrl()));
            }
            return users;
        }

        boolean requesterIncluded = false;
        for (String targetUserId : userIds) {
            conversationService.getActiveMembership(conversationId, targetUserId);
            var profile = userDirectoryService.getRequired(targetUserId);
            users.add(new User(profile.getId(), profile.getDisplayName(), profile.getAvatarUrl()));
            if (requesterId.equals(targetUserId)) {
                requesterIncluded = true;
            }
        }
        if (!requesterIncluded) {
            var profile = userDirectoryService.getRequired(requesterId);
            users.addFirst(new User(profile.getId(), profile.getDisplayName(), profile.getAvatarUrl()));
        }
        return users;
    }
}

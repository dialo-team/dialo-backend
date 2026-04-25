package com.fit.se.service;

import com.fit.se.dto.*;
import com.fit.se.entity.*;
import com.fit.se.repository.ConversationRepository;
import com.fit.se.repository.MessageRepository;
import com.fit.se.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private static final String TYPE_DIRECT = "direct";
    private static final String TYPE_GROUP = "group";
    private static final int MAX_GROUP_MEMBERS = 25;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final UserProfileService userProfileService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileStorageService fileStorageService;

    public List<GroupMemberResponse> getGroupMembers(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        validateParticipant(conversation, requesterId);
        return conversation.getParticipants().stream().map(userId -> toGroupMemberResponse(conversation, userId)).toList();
    }

    public List<GroupMemberResponse> searchGroupMembers(String conversationId, String requesterId, String keyword) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        validateParticipant(conversation, requesterId);
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return conversation.getParticipants().stream()
                .map(userId -> toGroupMemberResponse(conversation, userId))
                .filter(member -> normalizedKeyword.isBlank() || containsIgnoreCase(member.getDisplayName(), normalizedKeyword) || containsIgnoreCase(member.getNickname(), normalizedKeyword))
                .toList();
    }

    public List<GroupMemberHistoryResponse> getGroupHistory(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        validateParticipant(conversation, requesterId);
        return conversation.getMemberHistories().stream()
                .sorted(Comparator.comparing(GroupMemberHistory::getCreatedAt).reversed())
                .map(this::toGroupHistoryResponse)
                .toList();
    }

    public Conversation updateGroupName(String conversationId, String requesterId, UpdateGroupNameRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        conversation.setGroupName(requireText(request.getGroupName(), "Group name is required"));
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Tên nhóm đã được cập nhật");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation updateGroupDescription(String conversationId, String requesterId, UpdateGroupDescriptionRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        conversation.setGroupDescription(requireText(request.getGroupDescription(), "Group description is required"));
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Mô tả nhóm đã được cập nhật");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation updateMemberNickname(String conversationId, String requesterId, String memberId, UpdateMemberNicknameRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        if (!conversation.getParticipants().contains(memberId)) {
            throw new IllegalArgumentException("Member not found in group");
        }
        conversation.getMemberNicknames().put(memberId, requireText(request.getNickname(), "Nickname is required"));
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Biệt danh của " + resolveMemberName(saved, memberId) + " đã được cập nhật");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation updateGroupAvatar(String conversationId, String requesterId, MultipartFile file) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        FileStorageService.StoredFile storedFile = fileStorageService.store(file);
        conversation.setGroupAvatarUrl(storedFile.getPublicUrl());
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Ảnh đại diện nhóm đã được cập nhật");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation updateGroupAvatar(String conversationId, String requesterId, UpdateGroupAvatarRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        conversation.setGroupAvatarUrl(requireText(request.getGroupAvatarUrl(), "Group avatar url is required"));
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Ảnh đại diện nhóm đã được cập nhật");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation leaveGroup(String conversationId, String userId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        validateParticipant(conversation, userId);
        if ("OWNER".equals(getRole(conversation, userId))) {
            throw new IllegalArgumentException("Owner must transfer ownership before leaving group");
        }
        String memberName = resolveMemberName(conversation, userId);
        removeMemberData(conversation, userId);
        conversation.setUpdatedAt(Instant.now());
        appendGroupHistory(conversation, "LEFT", userId, resolveUserDisplayName(userId), userId, memberName, null, null, memberName + " đã rời nhóm");
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, memberName + " đã rời nhóm");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public void dissolveGroup(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwner(conversation, requesterId);
        conversation.setDissolved(true);
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Nhóm đã được giải tán");
        pushInboxUpdates(saved);
    }

    public Conversation createGroupConversation(String creatorId, CreateGroupRequest request) {
        String groupName = requireText(request.getName(), "Group name is required");
        Set<String> members = new HashSet<>(request.getMemberIds() == null ? List.of() : request.getMemberIds());
        members.add(creatorId);
        if (members.size() < 3) {
            throw new IllegalArgumentException("Group must have at least 3 members including creator");
        }
        if (members.size() > MAX_GROUP_MEMBERS) {
            throw new IllegalArgumentException("Group can contain at most " + MAX_GROUP_MEMBERS + " members");
        }
        members.forEach(userProfileService::findEntity);

        Instant now = Instant.now();
        Map<String, Integer> unreadCounts = new HashMap<>();
        Map<String, String> memberRoles = new HashMap<>();
        for (String memberId : members) {
            unreadCounts.put(memberId, 0);
            memberRoles.put(memberId, memberId.equals(creatorId) ? "OWNER" : "MEMBER");
        }

        Conversation conversation = Conversation.builder()
                .type(TYPE_GROUP)
                .participants(members.stream().sorted().toList())
                .groupName(groupName)
                .groupDescription("")
                .groupAvatarUrl(null)
                .dissolved(false)
                .memberRoles(memberRoles)
                .memberNicknames(new HashMap<>())
                .memberHistories(new ArrayList<>())
                .unreadCounts(unreadCounts)
                .remarks(new HashMap<>())
                .clearedAt(new HashMap<>())
                .createdBy(creatorId)
                .createdSource("manual")
                .createdAt(now)
                .updatedAt(now)
                .build();
        appendGroupHistory(conversation, "CREATED", creatorId, resolveUserDisplayName(creatorId), null, null, null, null, "Nhóm đã được tạo");
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Nhóm đã được tạo");
        return reloadConversation(saved);
    }

    public Conversation assignRole(String conversationId, String requesterId, String memberId, AssignRoleRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwner(conversation, requesterId);
        if (!conversation.getParticipants().contains(memberId)) {
            throw new IllegalArgumentException("Member not found in group");
        }
        String role = normalizeRole(request.getRole());
        String oldRole = getRole(conversation, memberId);
        if ("OWNER".equals(role)) {
            conversation.getMemberRoles().replaceAll((key, value) -> "OWNER".equals(value) ? "MEMBER" : value);
        }
        conversation.getMemberRoles().put(memberId, role);
        conversation.setUpdatedAt(Instant.now());
        appendGroupHistory(conversation, "ROLE_CHANGED", requesterId, resolveMemberName(conversation, requesterId), memberId, resolveMemberName(conversation, memberId), oldRole, role, "Vai trò của " + resolveMemberName(conversation, memberId) + " đã đổi từ " + defaultString(oldRole) + " sang " + role);
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, "Vai trò của " + resolveMemberName(saved, memberId) + " đã được đổi thành " + role);
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation addMembers(String conversationId, String requesterId, UpdateMembersRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
            throw new IllegalArgumentException("Member ids are required");
        }
        Set<String> uniqueMembers = new HashSet<>(request.getMemberIds());
        int nextSize = new HashSet<>(conversation.getParticipants()).size();
        for (String memberId : uniqueMembers) {
            if (!conversation.getParticipants().contains(memberId)) {
                nextSize++;
            }
        }
        if (nextSize > MAX_GROUP_MEMBERS) {
            throw new IllegalArgumentException("Group can contain at most " + MAX_GROUP_MEMBERS + " members");
        }

        List<String> addedMembers = new ArrayList<>();
        for (String memberId : uniqueMembers) {
            userProfileService.findEntity(memberId);
            if (!conversation.getParticipants().contains(memberId)) {
                conversation.getParticipants().add(memberId);
                conversation.getUnreadCounts().put(memberId, 0);
                conversation.getMemberRoles().put(memberId, "MEMBER");
                addedMembers.add(memberId);
                appendGroupHistory(conversation, "ADDED", requesterId, resolveMemberName(conversation, requesterId), memberId, resolveMemberName(conversation, memberId), null, "MEMBER", resolveMemberName(conversation, memberId) + " đã tham gia nhóm");
            }
        }
        conversation.setParticipants(conversation.getParticipants().stream().distinct().sorted().toList());
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        for (String memberId : addedMembers) {
            createSystemMessage(saved, resolveMemberName(saved, memberId) + " đã tham gia nhóm");
        }
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation removeMember(String conversationId, String requesterId, String memberId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateGroup(conversation);
        requireOwnerOrAdmin(conversation, requesterId);
        if (!conversation.getParticipants().contains(memberId)) {
            throw new IllegalArgumentException("Member not found in group");
        }
        if ("OWNER".equals(getRole(conversation, memberId))) {
            throw new IllegalArgumentException("Cannot remove owner");
        }
        String memberName = resolveMemberName(conversation, memberId);
        removeMemberData(conversation, memberId);
        conversation.setUpdatedAt(Instant.now());
        appendGroupHistory(conversation, "REMOVED", requesterId, resolveMemberName(conversation, requesterId), memberId, memberName, null, null, memberName + " đã bị xóa khỏi nhóm");
        Conversation saved = conversationRepository.save(conversation);
        createSystemMessage(saved, memberName + " đã bị xóa khỏi nhóm");
        pushInboxUpdates(saved);
        return reloadConversation(saved);
    }

    public Conversation createConversation(CreateConversationRequest request) {
        validateParticipants(request.getParticipantIds());
        String participantKey = buildParticipantKey(request.getParticipantIds());
        Optional<Conversation> existing = conversationRepository.findByParticipantKey(participantKey);
        if (existing.isPresent()) {
            return existing.get();
        }
        Instant now = Instant.now();
        Map<String, Integer> unreadCounts = new HashMap<>();
        request.getParticipantIds().forEach(id -> unreadCounts.put(id, 0));
        Conversation conversation = Conversation.builder()
                .type(TYPE_DIRECT)
                .participants(sortedParticipants(request.getParticipantIds()))
                .participantKey(participantKey)
                .remarks(new HashMap<>())
                .unreadCounts(unreadCounts)
                .clearedAt(new HashMap<>())
                .memberNicknames(new HashMap<>())
                .memberHistories(new ArrayList<>())
                .createdBy(request.getCreatedBy())
                .createdSource(request.getCreatedBy() == null ? "manual" : request.getCreatedBy())
                .createdAt(now)
                .updatedAt(now)
                .build();
        Conversation saved = conversationRepository.save(conversation);
        if (request.getInitialSystemMessage() != null && !request.getInitialSystemMessage().isBlank()) {
            createSystemMessage(saved, request.getInitialSystemMessage());
        } else {
            pushInboxUpdates(saved);
        }
        return reloadConversation(saved);
    }

    public List<ConversationSummaryResponse> getUserConversations(String userId) {
        userProfileService.findEntity(userId);
        return conversationRepository.findByParticipantsContainingOrderByUpdatedAtDesc(userId).stream().map(conversation -> toSummaryResponse(conversation, userId)).toList();
    }

    public ConversationDetailResponse getConversationDetail(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, requesterId);
        List<MessageResponse> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .filter(message -> shouldShowMessage(message, requesterId, conversation))
                .map(message -> toMessageResponse(message, requesterId, conversation))
                .toList();
        if (TYPE_GROUP.equalsIgnoreCase(conversation.getType())) {
            return ConversationDetailResponse.builder()
                    .conversationId(conversation.getId())
                    .requesterId(requesterId)
                    .counterpartId(conversation.getId())
                    .counterpartName(conversation.getGroupName())
                    .counterpartAvatarUrl(conversation.getGroupAvatarUrl())
                    .remarkName(null)
                    .groupDescription(conversation.getGroupDescription())
                    .pinnedMessages(toPinnedMessageResponses(conversation, requesterId))
                    .unreadCount(conversation.getUnreadCounts().getOrDefault(requesterId, 0))
                    .unreadDisplay(formatUnread(conversation.getUnreadCounts().getOrDefault(requesterId, 0)))
                    .createdAt(conversation.getCreatedAt())
                    .messages(messages)
                    .build();
        }
        UserProfile counterpart = getCounterpartProfile(conversation, requesterId);
        return ConversationDetailResponse.builder()
                .conversationId(conversation.getId())
                .requesterId(requesterId)
                .counterpartId(counterpart.getId())
                .counterpartName(resolveCounterpartName(conversation, requesterId, counterpart))
                .counterpartAvatarUrl(counterpart.getAvatarUrl())
                .remarkName(conversation.getRemarks().get(requesterId))
                .pinnedMessages(toPinnedMessageResponses(conversation, requesterId))
                .unreadCount(conversation.getUnreadCounts().getOrDefault(requesterId, 0))
                .unreadDisplay(formatUnread(conversation.getUnreadCounts().getOrDefault(requesterId, 0)))
                .createdAt(conversation.getCreatedAt())
                .messages(messages)
                .build();
    }

    public ConversationSummaryResponse updateRemark(String conversationId, UpdateRemarkRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, request.getRequesterId());
        conversation.getRemarks().put(request.getRequesterId(), request.getRemarkName());
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        pushInboxUpdates(saved);
        return toSummaryResponse(saved, request.getRequesterId());
    }

    public ConversationSummaryResponse markRead(String conversationId, MarkReadRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, request.getUserId());
        conversation.getUnreadCounts().put(request.getUserId(), 0);
        conversation.setUpdatedAt(Instant.now());
        updateReadReceipts(conversation, request.getUserId());
        Conversation saved = conversationRepository.save(conversation);
        pushInboxUpdates(saved);
        return toSummaryResponse(saved, request.getUserId());
    }

    public ConversationSummaryResponse clearHistory(String conversationId, ClearHistoryRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, request.getUserId());
        conversation.getClearedAt().put(request.getUserId(), Instant.now());
        conversation.setUnreadCounts(resetUnreadForUser(conversation.getUnreadCounts(), request.getUserId()));
        conversation.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(conversation);
        pushInboxUpdates(saved);
        return toSummaryResponse(saved, request.getUserId());
    }

    public List<MessageResponse> getMedia(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, requesterId);
        return messageRepository.findByConversationIdAndTypeInOrderByCreatedAtDesc(conversationId, List.of("IMAGE", "VIDEO", "FILE", "VOICE", "STICKER", "GIF", "EMOJI"))
                .stream().filter(message -> shouldShowMessage(message, requesterId, conversation))
                .map(message -> toMessageResponse(message, requesterId, conversation))
                .toList();
    }

    public List<MessageResponse> searchMessages(String conversationId, String requesterId, String keyword) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, requesterId);
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        String regex = ".*" + Pattern.quote(keyword.trim()) + ".*";
        return messageRepository.findByConversationIdAndContentRegexIgnoreCaseOrderByCreatedAtDesc(conversationId, regex)
                .stream().filter(message -> shouldShowMessage(message, requesterId, conversation))
                .map(message -> toMessageResponse(message, requesterId, conversation))
                .toList();
    }

    public void sendTypingIndicator(String conversationId, String userId, boolean typing) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, userId);
        messagingTemplate.convertAndSend("/topic/typing/" + conversationId, TypingIndicatorResponse.builder()
                .conversationId(conversationId)
                .userId(userId)
                .displayName(resolveMemberName(conversation, userId))
                .typing(typing)
                .occurredAt(Instant.now())
                .build());
    }

    public void pinMessage(String conversationId, String messageId, String userId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, userId);
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        if (!conversationId.equals(message.getConversationId())) {
            throw new IllegalArgumentException("Message does not belong to conversation");
        }
        boolean alreadyPinned = conversation.getPinnedMessages().stream().anyMatch(pinned -> pinned.getMessageId().equals(messageId));
        if (!alreadyPinned) {
            conversation.getPinnedMessages().add(PinnedMessageEntry.builder()
                    .messageId(messageId)
                    .pinnedByUserId(userId)
                    .pinnedAt(Instant.now())
                    .build());
        }
        conversationRepository.save(conversation);
    }

    public void unpinMessage(String conversationId, String messageId, String userId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, userId);
        conversation.getPinnedMessages().removeIf(pinned -> pinned.getMessageId().equals(messageId));
        conversationRepository.save(conversation);
    }

    public List<PinnedMessageResponse> getPinnedMessages(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        validateParticipant(conversation, requesterId);
        return toPinnedMessageResponses(conversation, requesterId);
    }

    public void clearPinnedMessageIfMatches(String conversationId, String messageId) {
        Conversation conversation = getConversationEntity(conversationId);
        boolean removed = conversation.getPinnedMessages().removeIf(pinned -> pinned.getMessageId().equals(messageId));
        if (removed) {
            conversationRepository.save(conversation);
        }
    }

    public Conversation getConversationEntity(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));
        normalizeConversation(conversation);
        return conversation;
    }

    public void validateParticipant(Conversation conversation, String userId) {
        normalizeConversation(conversation);
        if (!conversation.getParticipants().contains(userId)) {
            throw new IllegalArgumentException("User is not a participant of conversation");
        }
    }

    public Message createSystemMessage(Conversation conversation, String content) {
        Instant now = Instant.now();
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .type("SYSTEM")
                .content(content)
                .system(true)
                .readBy(buildReadByForSystem(conversation))
                .createdAt(now)
                .updatedAt(now)
                .build();
        Message saved = messageRepository.save(message);
        updateConversationLastMessage(conversation, saved, false);
        pushConversationEvent(saved, conversation);
        pushInboxUpdates(reloadConversation(conversation));
        return saved;
    }

    @RabbitListener(queues = "conversation.friend.accepted")
    public void handleFriendAccepted(FriendAcceptedEvent event) {
        if (event == null || event.getPayload() == null || event.getEventId() == null) {
            return;
        }
        if (processedEventRepository.existsById(event.getEventId())) {
            return;
        }
        CreateConversationRequest request = new CreateConversationRequest();
        request.setParticipantIds(List.of(event.getPayload().getUser1Id(), event.getPayload().getUser2Id()));
        request.setCreatedBy("system");
        request.setInitialSystemMessage(event.getPayload().getSystemMessage() == null || event.getPayload().getSystemMessage().isBlank() ? "Hai bạn đã trở thành bạn bè" : event.getPayload().getSystemMessage());
        createConversation(request);
        processedEventRepository.save(ProcessedEvent.builder().id(event.getEventId()).eventType(event.getEventType()).processedAt(Instant.now()).build());
    }

    public MessageResponse toMessageResponse(Message message, String requesterId) {
        Conversation conversation = getConversationEntity(message.getConversationId());
        return toMessageResponse(message, requesterId, conversation);
    }

    public MessageResponse toMessageResponse(Message message, String requesterId, Conversation conversation) {
        normalizeConversation(conversation);
        normalizeMessage(message);
        String senderName = "Hệ thống";
        String senderAvatarUrl = null;
        if (!message.isSystem() && message.getSenderId() != null) {
            UserProfile sender = userProfileService.findEntity(message.getSenderId());
            senderName = TYPE_GROUP.equalsIgnoreCase(conversation.getType()) ? conversation.getMemberNicknames().getOrDefault(message.getSenderId(), sender.getDisplayName()) : sender.getDisplayName();
            senderAvatarUrl = sender.getAvatarUrl();
        }
        String receiverName = null;
        if (message.getReceiverId() != null) {
            receiverName = userProfileService.findEntity(message.getReceiverId()).getDisplayName();
        }
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .senderName(senderName)
                .senderAvatarUrl(senderAvatarUrl)
                .receiverName(receiverName)
                .type(message.getType())
                .content(message.isRevoked() ? "Tin nhắn đã được thu hồi" : message.getContent())
                .attachment(message.getAttachment() == null ? null : AttachmentResponse.builder().fileName(message.getAttachment().getFileName()).fileUrl(message.getAttachment().getFileUrl()).mimeType(message.getAttachment().getMimeType()).size(message.getAttachment().getSize()).thumbnailUrl(message.getAttachment().getThumbnailUrl()).build())
                .revoked(message.isRevoked())
                .system(message.isSystem())
                .forwardedFromMessageId(message.getForwardedFromMessageId())
                .edited(message.isEdited())
                .editedAt(message.getEditedAt())
                .durationSeconds(message.getDurationSeconds())
                .location(message.getLocation())
                .contactCard(message.getContactCard())
                .poll(toPollResponse(message.getPoll()))
                .readReceipts(toReadReceipts(message))
                .reactions(toReactionResponses(message))
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .displayPosition(resolveDisplayPosition(message, requesterId))
                .build();
    }

    public ConversationSummaryResponse toSummaryResponse(Conversation conversation, String requesterId) {
        validateParticipant(conversation, requesterId);
        LastMessageSummary lastMessage = conversation.getLastMessage();
        ConversationSummaryResponse.ConversationSummaryResponseBuilder builder = ConversationSummaryResponse.builder()
                .conversationId(conversation.getId())
                .lastMessageId(lastMessage == null ? null : lastMessage.getMessageId())
                .lastMessage(lastMessage == null ? null : lastMessage.getContent())
                .lastMessageSenderId(lastMessage == null ? null : lastMessage.getSenderId())
                .lastMessageType(lastMessage == null ? null : lastMessage.getType())
                .lastMessageAt(lastMessage == null ? null : lastMessage.getCreatedAt())
                .lastMessageSystem(lastMessage == null ? null : lastMessage.getSystem())
                .unreadCount(conversation.getUnreadCounts().getOrDefault(requesterId, 0))
                .unreadDisplay(formatUnread(conversation.getUnreadCounts().getOrDefault(requesterId, 0)));
        if (TYPE_GROUP.equalsIgnoreCase(conversation.getType())) {
            builder.counterpartId(conversation.getId()).counterpartName(conversation.getGroupName()).counterpartAvatarUrl(conversation.getGroupAvatarUrl());
        } else {
            UserProfile counterpart = getCounterpartProfile(conversation, requesterId);
            builder.counterpartId(counterpart.getId()).counterpartName(resolveCounterpartName(conversation, requesterId, counterpart)).counterpartAvatarUrl(counterpart.getAvatarUrl());
        }
        return builder.build();
    }

    public void updateConversationLastMessage(Conversation conversation, Message message, boolean incrementUnread) {
        normalizeConversation(conversation);
        normalizeMessage(message);
        conversation.setLastMessage(LastMessageSummary.builder().messageId(message.getId()).senderId(message.getSenderId()).content(message.isRevoked() ? "Tin nhắn đã được thu hồi" : message.getContent()).type(message.getType()).system(message.isSystem()).createdAt(message.getCreatedAt()).build());
        conversation.setUpdatedAt(Instant.now());
        if (incrementUnread) {
            for (String participant : conversation.getParticipants()) {
                if (!participant.equals(message.getSenderId())) {
                    int current = conversation.getUnreadCounts().getOrDefault(participant, 0);
                    conversation.getUnreadCounts().put(participant, current + 1);
                }
            }
        }
        conversationRepository.save(conversation);
    }

    public void pushConversationEvent(Message message, Conversation conversation) {
        for (String participant : conversation.getParticipants()) {
            messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), toMessageResponse(message, participant, conversation));
        }
    }

    public void pushInboxUpdates(Conversation conversation) {
        normalizeConversation(conversation);
        for (String participant : conversation.getParticipants()) {
            messagingTemplate.convertAndSend("/topic/inbox/" + participant, getUserConversations(participant));
        }
    }

    public String buildParticipantKey(List<String> participantIds) {
        return String.join(":", sortedParticipants(participantIds));
    }

    private List<String> sortedParticipants(List<String> participantIds) {
        return participantIds.stream().sorted().toList();
    }

    private void validateParticipants(List<String> participantIds) {
        if (participantIds == null || participantIds.size() != 2) {
            throw new IllegalArgumentException("Direct conversation must contain exactly 2 participants");
        }
        if (new HashSet<>(participantIds).size() != 2) {
            throw new IllegalArgumentException("Participants must be different users");
        }
        participantIds.forEach(userProfileService::findEntity);
    }

    private UserProfile getCounterpartProfile(Conversation conversation, String requesterId) {
        String counterpartId = conversation.getParticipants().stream().filter(id -> !id.equals(requesterId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Counterpart not found"));
        return userProfileService.findEntity(counterpartId);
    }

    private String resolveCounterpartName(Conversation conversation, String requesterId, UserProfile counterpart) {
        return conversation.getRemarks().getOrDefault(requesterId, counterpart.getDisplayName());
    }

    private String resolveDisplayPosition(Message message, String requesterId) {
        if (message.isSystem() || "POLL".equalsIgnoreCase(message.getType())) {
            return "CENTER";
        }
        return requesterId.equals(message.getSenderId()) ? "RIGHT" : "LEFT";
    }

    private boolean shouldShowMessage(Message message, String requesterId, Conversation conversation) {
        normalizeMessage(message);
        if (message.getDeletedFor().contains(requesterId)) {
            return false;
        }
        Instant clearedAt = conversation.getClearedAt().get(requesterId);
        return clearedAt == null || !message.getCreatedAt().isBefore(clearedAt);
    }

    private String formatUnread(int unread) {
        return unread > 9 ? "9+" : String.valueOf(unread);
    }

    private Map<String, Integer> resetUnreadForUser(Map<String, Integer> unreadCounts, String userId) {
        Map<String, Integer> copy = new HashMap<>(unreadCounts);
        copy.put(userId, 0);
        return copy;
    }

    private void updateReadReceipts(Conversation conversation, String userId) {
        Instant now = Instant.now();
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        List<Message> changed = new ArrayList<>();
        for (Message message : messages) {
            normalizeMessage(message);
            if (message.getDeletedFor().contains(userId)) {
                continue;
            }
            if (message.getReadBy().putIfAbsent(userId, now) == null) {
                message.setUpdatedAt(now);
                changed.add(message);
            }
        }
        if (!changed.isEmpty()) {
            messageRepository.saveAll(changed);
        }
    }

    private void validateGroup(Conversation conversation) {
        normalizeConversation(conversation);
        if (!TYPE_GROUP.equalsIgnoreCase(conversation.getType())) {
            throw new IllegalArgumentException("Conversation is not a group");
        }
        if (Boolean.TRUE.equals(conversation.getDissolved())) {
            throw new IllegalArgumentException("Group has been dissolved");
        }
    }

    private String getRole(Conversation conversation, String userId) { return conversation.getMemberRoles().get(userId); }

    private void requireOwnerOrAdmin(Conversation conversation, String userId) {
        validateParticipant(conversation, userId);
        String role = getRole(conversation, userId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new IllegalArgumentException("Permission denied");
        }
    }

    private void requireOwner(Conversation conversation, String userId) {
        validateParticipant(conversation, userId);
        if (!"OWNER".equals(getRole(conversation, userId))) {
            throw new IllegalArgumentException("Only owner can perform this action");
        }
    }

    private void appendGroupHistory(Conversation conversation, String action, String actorUserId, String actorDisplayName, String targetUserId, String targetDisplayName, String oldRole, String newRole, String description) {
        conversation.getMemberHistories().add(GroupMemberHistory.builder().id(UUID.randomUUID().toString()).action(action).actorUserId(actorUserId).actorDisplayName(actorDisplayName).targetUserId(targetUserId).targetDisplayName(targetDisplayName).oldRole(oldRole).newRole(newRole).description(description).createdAt(Instant.now()).build());
    }

    private GroupMemberResponse toGroupMemberResponse(Conversation conversation, String userId) {
        UserProfile profile = userProfileService.findEntity(userId);
        return GroupMemberResponse.builder().userId(userId).displayName(profile.getDisplayName()).avatarUrl(profile.getAvatarUrl()).role(getRole(conversation, userId)).nickname(conversation.getMemberNicknames().get(userId)).build();
    }

    private GroupMemberHistoryResponse toGroupHistoryResponse(GroupMemberHistory history) {
        return GroupMemberHistoryResponse.builder().id(history.getId()).action(history.getAction()).actorUserId(history.getActorUserId()).actorDisplayName(history.getActorDisplayName()).targetUserId(history.getTargetUserId()).targetDisplayName(history.getTargetDisplayName()).oldRole(history.getOldRole()).newRole(history.getNewRole()).description(history.getDescription()).createdAt(history.getCreatedAt()).build();
    }

    private List<ReadReceiptResponse> toReadReceipts(Message message) {
        return message.getReadBy().entrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> {
            UserProfile profile = userProfileService.findEntity(entry.getKey());
            return ReadReceiptResponse.builder().userId(entry.getKey()).displayName(profile.getDisplayName()).avatarUrl(profile.getAvatarUrl()).readAt(entry.getValue()).build();
        }).toList();
    }

    private List<MessageReactionResponse> toReactionResponses(Message message) {
        return message.getReactions().stream().map(this::toReactionResponse).toList();
    }

    private MessageReactionResponse toReactionResponse(MessageReaction reaction) {
        UserProfile profile = userProfileService.findEntity(reaction.getUserId());
        return MessageReactionResponse.builder().userId(reaction.getUserId()).displayName(profile.getDisplayName()).avatarUrl(profile.getAvatarUrl()).emoji(reaction.getEmoji()).reactedAt(reaction.getReactedAt()).build();
    }

    private PollResponse toPollResponse(PollPayload poll) {
        if (poll == null) {
            return null;
        }
        return PollResponse.builder()
                .title(poll.getTitle())
                .closed(poll.isClosed())
                .closedAt(poll.getClosedAt())
                .closedByUserId(poll.getClosedByUserId())
                .options(poll.getOptions().stream()
                        .map(option -> PollOptionResponse.builder()
                                .id(option.getId())
                                .content(option.getContent())
                                .voters(option.getVoterIds().stream()
                                        .map(userProfileService::findEntity)
                                        .map(this::toUserProfileResponse)
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    private List<PinnedMessageResponse> toPinnedMessageResponses(Conversation conversation, String requesterId) {
        Map<String, Message> messageById = messageRepository.findAllById(
                        conversation.getPinnedMessages().stream()
                                .map(PinnedMessageEntry::getMessageId)
                                .distinct()
                                .toList())
                .stream()
                .collect(HashMap::new, (map, message) -> map.put(message.getId(), message), HashMap::putAll);

        return conversation.getPinnedMessages().stream()
                .sorted(Comparator.comparing(PinnedMessageEntry::getPinnedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(pinned -> {
                    Message message = messageById.get(pinned.getMessageId());
                    if (message == null || !shouldShowMessage(message, requesterId, conversation)) {
                        return null;
                    }
                    return PinnedMessageResponse.builder()
                            .messageId(pinned.getMessageId())
                            .pinnedByUserId(pinned.getPinnedByUserId())
                            .pinnedByName(resolveUserDisplayName(pinned.getPinnedByUserId()))
                            .pinnedAt(pinned.getPinnedAt())
                            .message(toMessageResponse(message, requesterId, conversation))
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private UserProfileResponse toUserProfileResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .build();
    }

    private void normalizeConversation(Conversation conversation) {
        if (conversation.getParticipants() == null) conversation.setParticipants(new ArrayList<>());
        if (conversation.getRemarks() == null) conversation.setRemarks(new HashMap<>());
        if (conversation.getUnreadCounts() == null) conversation.setUnreadCounts(new HashMap<>());
        if (conversation.getClearedAt() == null) conversation.setClearedAt(new HashMap<>());
        if (conversation.getMemberRoles() == null) conversation.setMemberRoles(new HashMap<>());
        if (conversation.getMemberNicknames() == null) conversation.setMemberNicknames(new HashMap<>());
        if (conversation.getMemberHistories() == null) conversation.setMemberHistories(new ArrayList<>());
        if (conversation.getPinnedMessages() == null) conversation.setPinnedMessages(new ArrayList<>());
    }

    private void normalizeMessage(Message message) {
        if (message.getDeletedFor() == null) message.setDeletedFor(new HashSet<>());
        if (message.getReadBy() == null) message.setReadBy(new HashMap<>());
        if (message.getReactions() == null) message.setReactions(new ArrayList<>());
        if (message.getPoll() != null && message.getPoll().getOptions() == null) message.getPoll().setOptions(new ArrayList<>());
    }

    private void removeMemberData(Conversation conversation, String memberId) {
        conversation.getParticipants().remove(memberId);
        conversation.getUnreadCounts().remove(memberId);
        conversation.getRemarks().remove(memberId);
        conversation.getClearedAt().remove(memberId);
        conversation.getMemberRoles().remove(memberId);
        conversation.getMemberNicknames().remove(memberId);
    }

    private Map<String, Instant> buildReadByForSystem(Conversation conversation) {
        Map<String, Instant> readBy = new HashMap<>();
        Instant now = Instant.now();
        for (String participant : conversation.getParticipants()) {
            readBy.put(participant, now);
        }
        return readBy;
    }

    private String normalizeRole(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase();
        if (!List.of("OWNER", "ADMIN", "MEMBER").contains(normalized)) {
            throw new IllegalArgumentException("Invalid role");
        }
        return normalized;
    }

    private String resolveMemberName(Conversation conversation, String userId) {
        String nickname = conversation.getMemberNicknames().get(userId);
        if (nickname != null && !nickname.isBlank()) {
            return nickname;
        }
        return resolveUserDisplayName(userId);
    }

    private String resolveUserDisplayName(String userId) { return userProfileService.findEntity(userId).getDisplayName(); }

    private String requireText(String value, String message) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) throw new IllegalArgumentException(message);
        return normalized;
    }

    private String defaultString(String value) { return value == null ? "KHÔNG_XÁC_ĐỊNH" : value; }

    private boolean containsIgnoreCase(String source, String keyword) { return source != null && source.toLowerCase().contains(keyword); }

    private Conversation reloadConversation(Conversation conversation) {
        return conversationRepository.findById(conversation.getId()).map(found -> {
            normalizeConversation(found);
            return found;
        }).orElse(conversation);
    }
}

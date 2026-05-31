package com.fit.se.conversation.service;

import com.fit.se.conversation.dto.ConversationDetailResponse;
import com.fit.se.conversation.dto.ConversationMemberResponse;
import com.fit.se.conversation.dto.ConversationSummaryResponse;
import com.fit.se.conversation.dto.AddGroupMembersRequest;
import com.fit.se.conversation.dto.CreateConversationRequest;
import com.fit.se.conversation.dto.CreateGroupConversationRequest;
import com.fit.se.conversation.dto.DissolveGroupResponse;
import com.fit.se.conversation.dto.LeaveGroupResponse;
import com.fit.se.message.dto.MessageReadByResponse;
import com.fit.se.conversation.dto.RemoveGroupMemberResponse;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleRequest;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleResponse;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.conversation.dto.TypingIndicatorResponse;
import com.fit.se.conversation.dto.TransferGroupOwnerRequest;
import com.fit.se.conversation.dto.TransferGroupOwnerResponse;
import com.fit.se.attachment.domain.Attachment;
import com.fit.se.event.domain.ConsumedEvent;
import com.fit.se.user.domain.User;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.conversation.domain.ConversationStatus;
import com.fit.se.conversation.domain.ConversationType;
import com.fit.se.conversation.domain.Member;
import com.fit.se.conversation.domain.MemberRole;
import com.fit.se.message.domain.Message;
import com.fit.se.message.domain.Reaction;
import com.fit.se.message.domain.MessageType;
import com.fit.se.poll.domain.Poll;
import com.fit.se.poll.domain.PollResults;
import com.fit.se.event.model.FriendAcceptedEvent;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.event.domain.ConsumedEventRepository;
import com.fit.se.conversation.repository.ConversationRepository;
import com.fit.se.conversation.repository.MemberRepository;
import com.fit.se.message.repository.MessageHiddenRepository;
import com.fit.se.message.repository.MessageReactionRepository;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.poll.repository.PollVoteRepository;
import com.fit.se.user.service.UserDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MessageHiddenRepository messageHiddenRepository;
    private final MessageReactionRepository messageReactionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final ConsumedEventRepository consumedEventRepository;
    private final UserDirectoryService userDirectoryService;
    private final SimpMessagingTemplate messagingTemplate;

    public List<ConversationSummaryResponse> getUserConversations(String userId) {
        List<Member> memberships = memberRepository.findByUserIdAndLeftAtIsNull(userId);
        if (memberships.isEmpty()) {
            return List.of();
        }

        Map<String, Member> memberByConversationId = new HashMap<>();
        List<String> conversationIds = new ArrayList<>();
        for (Member membership : memberships) {
            memberByConversationId.put(membership.getConversationId(), membership);
            conversationIds.add(membership.getConversationId());
        }

        List<Conversation> conversations = conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(conversationIds, ConversationStatus.ACTIVE);
        return conversations.stream()
                .map(conversation -> toSummaryResponse(conversation, memberByConversationId.get(conversation.getId()), userId))
                .filter(Objects::nonNull)
                .toList();
    }

    public ConversationDetailResponse getConversationDetail(String conversationId, String userId) {
        Conversation conversation = getConversationEntity(conversationId);
        Member requesterMembership = getActiveMembership(conversationId, userId);
        List<String> hiddenMessageIds = getHiddenMessageIds(userId, conversationId);
        List<MessageResponse> messages = messageRepository.findByConversationIdOrderByPositionAsc(conversationId).stream()
                .filter(message -> !hiddenMessageIds.contains(message.getId()))
                .map(message -> toMessageResponse(message, userId))
                .toList();
        List<Member> activeMembers = conversation.getType() == ConversationType.GROUP
                ? getActiveMembers(conversationId)
                : List.of();
        Member counterpart = conversation.getType() == ConversationType.DIRECT ? getCounterpartMember(conversationId, userId) : null;
        return ConversationDetailResponse.builder()
                .conversationId(conversation.getId())
                .type(conversation.getType() == null ? null : conversation.getType().name())
                .counterpartId(counterpart == null ? null : counterpart.getUserId())
                .counterpartName(resolveDisplayName(counterpart))
                .counterpartAvatarUrl(counterpart == null ? null : counterpart.getAvatarUrl())
                .groupName(conversation.getType() == ConversationType.GROUP ? conversation.getName() : null)
                .groupAvatarUrl(conversation.getType() == ConversationType.GROUP ? conversation.getAvatarUrl() : null)
                .ownerId(conversation.getType() == ConversationType.GROUP ? conversation.getOwnerId() : null)
                .description(conversation.getType() == ConversationType.GROUP ? conversation.getDescription() : null)
                .createdAt(conversation.getCreatedAt())
                .activeMemberCount(conversation.getType() == ConversationType.GROUP ? activeMembers.size() : null)
                .myRole(conversation.getType() == ConversationType.GROUP && requesterMembership.getRole() != null ? requesterMembership.getRole().name() : null)
                .blocked(false)
                .blockedMessage(null)
                .members(conversation.getType() == ConversationType.GROUP ? activeMembers.stream().map(this::toConversationMemberResponse).toList() : List.of())
                .unreadCount(calculateUnreadCount(conversationId, requesterMembership, userId))
                .messages(messages)
                .build();
    }

    public ConversationSummaryResponse createDirectConversation(String requesterId, CreateConversationRequest request) {
        validateDirectParticipants(requesterId, request.getParticipantIds());
        Conversation conversation = ensureDirectConversation(request.getParticipantIds().get(0), request.getParticipantIds().get(1));
        return toSummaryResponse(conversation, getActiveMembership(conversation.getId(), requesterId), requesterId);
    }

    public ConversationSummaryResponse createGroupConversation(String requesterId, CreateGroupConversationRequest request) {
        validateGroupParticipants(requesterId, request);

        Instant now = Instant.now();
        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.GROUP);
        conversation.setName(request.getName().trim());
        conversation.setAvatarUrl(request.getAvatarUrl());
        conversation.setDescription(request.getDescription());
        conversation.setOwnerId(requesterId);
        conversation.setCreatedAt(now);
        conversation.setUpdatedAt(now);
        conversation.setLastMessageAt(now);
        conversation.setStatus(ConversationStatus.ACTIVE);
        Conversation saved = conversationRepository.save(conversation);

        List<String> participantIds = new ArrayList<>(request.getParticipantIds());
        if (!participantIds.contains(requesterId)) {
            participantIds.add(requesterId);
        }

        for (String participantId : participantIds.stream().filter(Objects::nonNull).filter(item -> !item.isBlank()).distinct().toList()) {
            createMembership(saved.getId(), participantId, participantId.equals(requesterId) ? MemberRole.OWNER : MemberRole.MEMBER, now);
        }

        pushInboxUpdates(saved);
        return toSummaryResponse(saved, getActiveMembership(saved.getId(), requesterId), requesterId);
    }

    public ConversationSummaryResponse addGroupMembers(String conversationId, String requesterId, AddGroupMembersRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        if (conversation.getType() != ConversationType.GROUP) {
            throw new IllegalArgumentException("Chi ho tro them thanh vien vao group");
        }

        Member requesterMembership = getActiveMembership(conversationId, requesterId);
        if (requesterMembership.getRole() != MemberRole.OWNER && requesterMembership.getRole() != MemberRole.ADMIN) {
            throw new IllegalArgumentException("Ban khong co quyen them thanh vien");
        }
        if (request == null || request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Can it nhat mot thanh vien de them vao nhom");
        }

        Instant now = Instant.now();
        for (String participantId : request.getParticipantIds().stream().filter(Objects::nonNull).filter(item -> !item.isBlank()).distinct().toList()) {
            if (participantId.equals(requesterId)) {
                continue;
            }
            userDirectoryService.getRequired(participantId);
            if (memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull(conversationId, participantId).isPresent()) {
                continue;
            }
            createMembership(conversationId, participantId, MemberRole.MEMBER, now);
        }

        pushInboxUpdates(conversation);
        return toSummaryResponse(conversation, requesterMembership, requesterId);
    }

    public RemoveGroupMemberResponse removeGroupMember(String conversationId, String requesterId, String memberUserId) {
        Conversation conversation = getConversationEntity(conversationId);
        if (conversation.getType() != ConversationType.GROUP) {
            throw new IllegalArgumentException("Chi ho tro xoa thanh vien khoi group");
        }
        if (memberUserId == null || memberUserId.isBlank()) {
            throw new IllegalArgumentException("User can xoa khong hop le");
        }
        if (requesterId.equals(memberUserId)) {
            throw new IllegalArgumentException("Vui long dung flow roi nhom cho chinh minh");
        }

        Member requesterMembership = getActiveMembership(conversationId, requesterId);
        Member targetMembership = getActiveMembership(conversationId, memberUserId);

        if (requesterMembership.getRole() != MemberRole.OWNER && requesterMembership.getRole() != MemberRole.ADMIN) {
            throw new IllegalArgumentException("Ban khong co quyen xoa thanh vien");
        }
        if (targetMembership.getRole() == MemberRole.OWNER) {
            throw new IllegalArgumentException("Khong the xoa owner khoi nhom");
        }
        if (requesterMembership.getRole() == MemberRole.ADMIN && targetMembership.getRole() != MemberRole.MEMBER) {
            throw new IllegalArgumentException("Admin chi duoc xoa member");
        }

        targetMembership.setLeftAt(Instant.now());
        memberRepository.save(targetMembership);
        pushInboxUpdates(conversation);

        return RemoveGroupMemberResponse.builder()
                .conversationId(conversationId)
                .removedUserId(memberUserId)
                .removed(true)
                .build();
    }

    public UpdateGroupMemberRoleResponse updateGroupMemberRole(
            String conversationId,
            String requesterId,
            String memberUserId,
            UpdateGroupMemberRoleRequest request
    ) {
        Conversation conversation = getConversationEntity(conversationId);
        if (conversation.getType() != ConversationType.GROUP) {
            throw new IllegalArgumentException("Chi ho tro phan quyen trong group");
        }
        if (request == null || request.getRole() == null || request.getRole().isBlank()) {
            throw new IllegalArgumentException("Role khong hop le");
        }

        Member requesterMembership = getActiveMembership(conversationId, requesterId);
        if (requesterMembership.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("Chi owner moi duoc phan quyen");
        }

        Member targetMembership = getActiveMembership(conversationId, memberUserId);
        if (targetMembership.getRole() == MemberRole.OWNER) {
            throw new IllegalArgumentException("Khong the thay doi role cua owner");
        }

        MemberRole targetRole;
        try {
            targetRole = MemberRole.valueOf(request.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Role khong duoc ho tro");
        }

        if (targetRole == MemberRole.OWNER) {
            throw new IllegalArgumentException("Khong ho tro chuyen owner trong flow nay");
        }

        targetMembership.setRole(targetRole);
        memberRepository.save(targetMembership);
        pushInboxUpdates(conversation);

        return UpdateGroupMemberRoleResponse.builder()
                .conversationId(conversationId)
                .userId(memberUserId)
                .role(targetRole.name())
                .build();
    }

    public LeaveGroupResponse leaveGroup(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        if (conversation.getType() != ConversationType.GROUP) {
            throw new IllegalArgumentException("Chi ho tro roi khoi group");
        }

        Member requesterMembership = getActiveMembership(conversationId, requesterId);
        if (requesterMembership.getRole() == MemberRole.OWNER) {
            throw new IllegalArgumentException("Owner khong the roi nhom trong flow nay");
        }

        requesterMembership.setLeftAt(Instant.now());
        memberRepository.save(requesterMembership);
        pushInboxUpdates(conversation);

        return LeaveGroupResponse.builder()
                .conversationId(conversationId)
                .userId(requesterId)
                .left(true)
                .build();
    }

    public TransferGroupOwnerResponse transferGroupOwner(String conversationId, String requesterId, TransferGroupOwnerRequest request) {
        Conversation conversation = getConversationEntity(conversationId);
        if (conversation.getType() != ConversationType.GROUP) {
            throw new IllegalArgumentException("Chi ho tro chuyen owner trong group");
        }
        if (request == null || request.getNewOwnerUserId() == null || request.getNewOwnerUserId().isBlank()) {
            throw new IllegalArgumentException("User owner moi khong hop le");
        }

        Member requesterMembership = getActiveMembership(conversationId, requesterId);
        if (requesterMembership.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("Chi owner moi duoc chuyen owner");
        }

        String newOwnerUserId = request.getNewOwnerUserId().trim();
        if (requesterId.equals(newOwnerUserId)) {
            throw new IllegalArgumentException("Owner moi phai khac owner hien tai");
        }

        Member targetMembership = getActiveMembership(conversationId, newOwnerUserId);
        requesterMembership.setRole(MemberRole.ADMIN);
        targetMembership.setRole(MemberRole.OWNER);
        memberRepository.save(requesterMembership);
        memberRepository.save(targetMembership);

        conversation.setOwnerId(newOwnerUserId);
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);
        pushInboxUpdates(conversation);

        return TransferGroupOwnerResponse.builder()
                .conversationId(conversationId)
                .previousOwnerUserId(requesterId)
                .newOwnerUserId(newOwnerUserId)
                .transferred(true)
                .build();
    }

    public DissolveGroupResponse dissolveGroup(String conversationId, String requesterId) {
        Conversation conversation = getConversationEntity(conversationId);
        if (conversation.getType() != ConversationType.GROUP) {
            throw new IllegalArgumentException("Chi ho tro giai tan group");
        }

        Member requesterMembership = getActiveMembership(conversationId, requesterId);
        if (requesterMembership.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("Chi owner moi duoc giai tan nhom");
        }

        conversation.setStatus(ConversationStatus.DELETE);
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);
        pushInboxUpdates(conversation);

        return DissolveGroupResponse.builder()
                .conversationId(conversationId)
                .dissolved(true)
                .build();
    }

    public ConversationSummaryResponse markRead(String conversationId, String userId) {
        Conversation conversation = getConversationEntity(conversationId);
        Member membership = getActiveMembership(conversationId, userId);
        Optional<Message> lastMessage = messageRepository.findTopByConversationIdOrderByPositionDesc(conversationId);
        if (lastMessage.isPresent()) {
            membership.setLastReadPosition(lastMessage.get().getPosition());
            membership.setLastReadMessageId(lastMessage.get().getId());
            membership.setLastReadAt(Instant.now());
            memberRepository.save(membership);
            publishMessageUpdateEvent(lastMessage.get());
        }
        pushInboxUpdates(conversation);
        return toSummaryResponse(conversation, membership, userId);
    }

    public void sendTypingIndicator(String conversationId, String userId, boolean typing) {
        getActiveMembership(conversationId, userId);
        UserProfileDocument profile = userDirectoryService.getOrCreate(userId);
        messagingTemplate.convertAndSend("/topic/typing/" + conversationId, TypingIndicatorResponse.builder()
                .conversationId(conversationId)
                .userId(userId)
                .displayName(resolveProfileDisplayName(profile, userId))
                .typing(typing)
                .occurredAt(Instant.now())
                .build());
    }

    public Conversation ensureDirectConversation(String user1Id, String user2Id) {
        if (user1Id == null || user1Id.isBlank() || user2Id == null || user2Id.isBlank()) {
            throw new IllegalArgumentException("Id nguoi dung khong duoc de trong");
        }
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("Khong the tao direct conversation voi chinh minh");
        }

        userDirectoryService.getRequired(user1Id);
        userDirectoryService.getRequired(user2Id);

        String directKey = buildDirectKey(user1Id, user2Id);
        Optional<Conversation> existing = conversationRepository.findByTypeAndDirectKeyAndStatus(ConversationType.DIRECT, directKey, ConversationStatus.ACTIVE);
        if (existing.isPresent()) {
            return existing.get();
        }

        Instant now = Instant.now();
        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.DIRECT);
        conversation.setDirectKey(directKey);
        conversation.setCreatedAt(now);
        conversation.setUpdatedAt(now);
        conversation.setStatus(ConversationStatus.ACTIVE);
        Conversation saved = conversationRepository.save(conversation);

        createMembership(saved.getId(), user1Id, MemberRole.MEMBER, now);
        createMembership(saved.getId(), user2Id, MemberRole.MEMBER, now);
        pushInboxUpdates(saved);
        return saved;
    }

    public Conversation getConversationEntity(String conversationId) {
        return conversationRepository.findByIdAndStatus(conversationId, ConversationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay conversation"));
    }

    public Member getActiveMembership(String conversationId, String userId) {
        return memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nguoi dung khong nam trong conversation"));
    }

    public List<Member> getActiveMembers(String conversationId) {
        return memberRepository.findByConversationIdAndLeftAtIsNull(conversationId);
    }

    public String resolveMemberDisplayName(Member member) {
        return resolveDisplayName(member);
    }

    public MessageResponse toMessageResponse(Message message, String requesterId) {
        boolean system = message.getType() == MessageType.SYSTEM;
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getUser() == null ? null : message.getUser().getUserId())
                .senderName(system ? "He thong" : (message.getUser() == null ? null : message.getUser().getNick()))
                .senderAvatarUrl(system ? null : (message.getUser() == null ? null : message.getUser().getAvatarUrl()))
                .type(message.getType() == null ? null : message.getType().name())
                .content(message.getContent())
                .system(system)
                .position(message.getPosition())
                .attachments(message.getAttachments())
                .stickers(message.getStickers())
                .mentions(message.getMentions())
                .mentionEveryone(message.getMentionEveryone())
                .reference(message.getReference())
                .poll(resolvePoll(message, requesterId))
                .call(message.getCall())
                .reactions(resolveReactions(message.getId(), requesterId))
                .readBy(resolveReadBy(message))
                .pinned(Boolean.TRUE.equals(message.getPinned()))
                .createdAt(message.getTimeStamp())
                .editedTimeStamp(message.getEditedTimeStamp())
                .deletedTimeStamp(message.getDeletedTimeStamp())
                .deleted(message.getDeletedTimeStamp() != null)
                .deletedForEveryone(Boolean.TRUE.equals(message.getDeletedForEveryone()))
                .displayPosition(system ? "CENTER" : resolveDisplayPosition(message, requesterId))
                .build();
    }

    public void publishMessageEvent(Conversation conversation, Message message) {
        messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), toMessageResponse(message, null));
        pushInboxUpdates(conversation);
    }

    public void publishMessageUpdateEvent(Message message) {
        messagingTemplate.convertAndSend("/topic/conversations/" + message.getConversationId(), toMessageResponse(message, null));
    }

    public void pushInboxUpdateForUser(String conversationId, String userId) {
        getActiveMembership(conversationId, userId);
        messagingTemplate.convertAndSend("/topic/inbox/" + userId, getUserConversations(userId));
    }

    @RabbitListener(queues = "messaging.friend.accepted")
    public void handleFriendAccepted(FriendAcceptedEvent event) {
        if (event == null || event.getEventId() == null || event.getEventId().isBlank() || event.getPayload() == null) {
            return;
        }
        if (consumedEventRepository.existsById(event.getEventId())) {
            return;
        }

        String user1Id = event.getPayload().getUser1Id();
        String user2Id = event.getPayload().getUser2Id();
        if (user1Id == null || user1Id.isBlank() || user2Id == null || user2Id.isBlank()) {
            return;
        }

        Conversation conversation = ensureDirectConversation(user1Id, user2Id);
        String content = event.getPayload().getSystemMessage() == null || event.getPayload().getSystemMessage().isBlank()
                ? "Hai ban da tro thanh ban be"
                : event.getPayload().getSystemMessage();
        createSystemMessage(conversation, content);

        ConsumedEvent consumedEvent = new ConsumedEvent();
        consumedEvent.setId(event.getEventId());
        consumedEvent.setEventType(event.getEventType());
        consumedEvent.setProcessedAt(Instant.now());
        consumedEventRepository.save(consumedEvent);
    }

    public String buildDirectKey(String user1Id, String user2Id) {
        return List.of(user1Id, user2Id).stream().sorted().reduce((left, right) -> left + ":" + right).orElseThrow();
    }

    private void createMembership(String conversationId, String userId, MemberRole role, Instant joinedAt) {
        UserProfileDocument profile = userDirectoryService.getOrCreate(userId);
        Member member = new Member();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setNick(profile.getDisplayName());
        member.setAvatarUrl(profile.getAvatarUrl());
        member.setRole(role);
        member.setJoinedAt(joinedAt);
        member.setMuted(false);
        member.setPinned(false);
        member.setArchived(false);
        member.setLastReadPosition(0L);
        memberRepository.save(member);
    }

    private void validateDirectParticipants(String requesterId, List<String> participantIds) {
        if (participantIds == null || participantIds.size() != 2) {
            throw new IllegalArgumentException("Direct conversation phai co dung 2 nguoi tham gia");
        }
        if (new HashSet<>(participantIds).size() != 2) {
            throw new IllegalArgumentException("Hai nguoi tham gia phai khac nhau");
        }
        if (!participantIds.contains(requesterId)) {
            throw new IllegalArgumentException("Nguoi tao conversation phai nam trong danh sach participant");
        }
        participantIds.forEach(userDirectoryService::getRequired);
    }

    private void validateGroupParticipants(String requesterId, CreateGroupConversationRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Ten nhom khong duoc de trong");
        }
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Nhom phai co it nhat 3 thanh vien");
        }

        List<String> participantIds = new ArrayList<>(request.getParticipantIds());
        if (!participantIds.contains(requesterId)) {
            participantIds.add(requesterId);
        }

        List<String> distinctParticipants = participantIds.stream()
                .filter(Objects::nonNull)
                .filter(item -> !item.isBlank())
                .distinct()
                .toList();
        if (distinctParticipants.size() < 3) {
            throw new IllegalArgumentException("Nhom phai co it nhat 3 thanh vien");
        }
        distinctParticipants.forEach(userDirectoryService::getRequired);
    }

    private ConversationSummaryResponse toSummaryResponse(Conversation conversation, Member requesterMembership, String requesterId) {
        if (conversation == null || requesterMembership == null) {
            return null;
        }
        Member counterpart = conversation.getType() == ConversationType.DIRECT ? getCounterpartMember(conversation.getId(), requesterId) : null;
        Message lastMessage = resolveLatestVisibleMessage(conversation, requesterId);
        int unreadCount = calculateUnreadCount(conversation.getId(), requesterMembership, requesterId);
        String lastMessagePreview = buildLastMessagePreview(lastMessage);
        return ConversationSummaryResponse.builder()
                .conversationId(conversation.getId())
                .type(conversation.getType() == null ? null : conversation.getType().name())
                .counterpartId(counterpart == null ? null : counterpart.getUserId())
                .counterpartName(resolveDisplayName(counterpart))
                .counterpartAvatarUrl(counterpart == null ? null : counterpart.getAvatarUrl())
                .groupName(conversation.getType() == ConversationType.GROUP ? conversation.getName() : null)
                .groupAvatarUrl(conversation.getType() == ConversationType.GROUP ? conversation.getAvatarUrl() : null)
                .ownerId(conversation.getType() == ConversationType.GROUP ? conversation.getOwnerId() : null)
                .description(conversation.getType() == ConversationType.GROUP ? conversation.getDescription() : null)
                .activeMemberCount(conversation.getType() == ConversationType.GROUP ? getActiveMembers(conversation.getId()).size() : null)
                .myRole(conversation.getType() == ConversationType.GROUP && requesterMembership.getRole() != null ? requesterMembership.getRole().name() : null)
                .blocked(false)
                .lastMessageId(lastMessage == null ? null : lastMessage.getId())
                .lastMessage(lastMessagePreview)
                .lastMessagePreview(lastMessagePreview)
                .lastMessageSenderId(lastMessage == null || lastMessage.getUser() == null ? null : lastMessage.getUser().getUserId())
                .lastMessageType(lastMessage == null ? null : lastMessage.getType().name())
                .lastMessageAt(conversation.getLastMessageAt())
                .lastMessageSystem(lastMessage != null && lastMessage.getType() == MessageType.SYSTEM)
                .unreadCount(unreadCount)
                .unreadDisplay(formatUnread(unreadCount))
                .build();
    }

    private Member getCounterpartMember(String conversationId, String requesterId) {
        return memberRepository.findByConversationIdAndLeftAtIsNull(conversationId).stream()
                .filter(member -> !member.getUserId().equals(requesterId))
                .findFirst()
                .orElse(null);
    }

    private String resolveDisplayName(Member counterpart) {
        if (counterpart == null) {
            return null;
        }
        return counterpart.getNick() == null || counterpart.getNick().isBlank() ? counterpart.getUserId() : counterpart.getNick();
    }

    private ConversationMemberResponse toConversationMemberResponse(Member member) {
        return ConversationMemberResponse.builder()
                .userId(member.getUserId())
                .displayName(resolveDisplayName(member))
                .avatarUrl(member.getAvatarUrl())
                .role(member.getRole() == null ? null : member.getRole().name())
                .joinedAt(member.getJoinedAt())
                .leftAt(member.getLeftAt())
                .active(member.getLeftAt() == null)
                .build();
    }

    private String resolveProfileDisplayName(UserProfileDocument profile, String userId) {
        if (profile == null || profile.getDisplayName() == null || profile.getDisplayName().isBlank()) {
            return userId;
        }
        return profile.getDisplayName();
    }

    private int calculateUnreadCount(String conversationId, Member membership, String requesterId) {
        long lastReadPosition = membership.getLastReadPosition() == null ? 0L : membership.getLastReadPosition();
        List<String> hiddenMessageIds = getHiddenMessageIds(requesterId, conversationId);
        return (int) messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc(conversationId, lastReadPosition).stream()
                .filter(message -> !hiddenMessageIds.contains(message.getId()))
                .filter(message -> message.getUser() == null || !requesterId.equals(message.getUser().getUserId()))
                .count();
    }

    private String formatUnread(int unreadCount) {
        return unreadCount > 9 ? "9+" : String.valueOf(unreadCount);
    }

    private String buildLastMessagePreview(Message lastMessage) {
        if (lastMessage == null) {
            return null;
        }

        String content = lastMessage.getContent();
        if (content != null && !content.isBlank()) {
            return content;
        }

        if (lastMessage.getDeletedTimeStamp() != null) {
            return Boolean.TRUE.equals(lastMessage.getDeletedForEveryone())
                    ? "Tin nhan da bi xoa"
                    : "Tin nhan da duoc thu hoi";
        }

        if (lastMessage.getPoll() != null) {
            return buildPollPreview(lastMessage.getPoll());
        }

        if (lastMessage.getStickers() != null && !lastMessage.getStickers().isEmpty()) {
            return "Sticker";
        }

        if (lastMessage.getAttachments() != null && !lastMessage.getAttachments().isEmpty()) {
            return buildAttachmentPreview(lastMessage.getAttachments());
        }

        if (lastMessage.getCall() != null) {
            return "Cuoc goi";
        }

        return null;
    }

    private String buildPollPreview(Poll poll) {
        if (poll == null) {
            return null;
        }
        String question = poll.getQuestion();
        return question == null || question.isBlank() ? "Binh chon" : question;
    }

    private String buildAttachmentPreview(List<Attachment> attachments) {
        Attachment firstAttachment = attachments.getFirst();
        String contentType = firstAttachment.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return "Anh";
        }

        String fileName = firstAttachment.getFileName();
        if (fileName != null && !fileName.isBlank()) {
            return "File - " + fileName;
        }
        return "File";
    }

    private String resolveDisplayPosition(Message message, String requesterId) {
        if (message.getUser() == null) {
            return "CENTER";
        }
        if (requesterId == null || requesterId.isBlank()) {
            return null;
        }
        return requesterId.equals(message.getUser().getUserId()) ? "RIGHT" : "LEFT";
    }

    private void createSystemMessage(Conversation conversation, String content) {
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setType(MessageType.SYSTEM);
        message.setPosition(nextPosition(conversation.getId()));
        message.setContent(content);
        message.setTimeStamp(Instant.now());
        Message saved = messageRepository.save(message);
        conversation.setLastMessageId(saved.getId());
        conversation.setLastMessageAt(saved.getTimeStamp());
        conversation.setUpdatedAt(saved.getTimeStamp());
        conversationRepository.save(conversation);
        publishMessageEvent(conversation, saved);
    }

    public long nextPosition(String conversationId) {
        return messageRepository.findTopByConversationIdOrderByPositionDesc(conversationId)
                .map(message -> message.getPosition() == null ? 1L : message.getPosition() + 1)
                .orElse(1L);
    }

    public void updateConversationAfterMessage(Conversation conversation, Message message) {
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageAt(message.getTimeStamp());
        conversation.setUpdatedAt(message.getTimeStamp());
        conversationRepository.save(conversation);
    }

    private void pushInboxUpdates(Conversation conversation) {
        List<Member> memberships = memberRepository.findByConversationIdAndLeftAtIsNull(conversation.getId());
        for (Member membership : memberships) {
            messagingTemplate.convertAndSend("/topic/inbox/" + membership.getUserId(), getUserConversations(membership.getUserId()));
        }
    }

    private Message resolveLatestVisibleMessage(Conversation conversation, String requesterId) {
        if (conversation == null) {
            return null;
        }
        List<String> hiddenMessageIds = getHiddenMessageIds(requesterId, conversation.getId());
        if (hiddenMessageIds.isEmpty()) {
            return conversation.getLastMessageId() == null ? null : messageRepository.findById(conversation.getLastMessageId()).orElse(null);
        }
        List<Message> visibleMessages = messageRepository.findByConversationIdOrderByPositionAsc(conversation.getId()).stream()
                .filter(message -> !hiddenMessageIds.contains(message.getId()))
                .toList();
        return visibleMessages.isEmpty() ? null : visibleMessages.getLast();
    }

    private List<String> getHiddenMessageIds(String userId, String conversationId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        return messageHiddenRepository.findByUserIdAndConversationId(userId, conversationId).stream()
                .map(com.fit.se.message.domain.MessageHidden::getMessageId)
                .toList();
    }

    private List<Reaction> resolveReactions(String messageId, String requesterId) {
        if (messageId == null || messageId.isBlank()) {
            return List.of();
        }

        Map<Integer, Integer> countByEmoji = new HashMap<>();
        Map<Integer, Boolean> requesterReacted = new HashMap<>();
        List<com.fit.se.message.domain.MessageReaction> storedReactions = messageReactionRepository.findByMessageId(messageId);
        if (storedReactions == null || storedReactions.isEmpty()) {
            return List.of();
        }

        storedReactions.forEach(item -> {
            countByEmoji.merge(item.getEmoji(), 1, Integer::sum);
            if (requesterId != null && requesterId.equals(item.getUserId())) {
                requesterReacted.put(item.getEmoji(), true);
            }
        });

        return countByEmoji.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new Reaction(entry.getValue(), entry.getKey(), requesterReacted.getOrDefault(entry.getKey(), false)))
                .toList();
    }

    private List<MessageReadByResponse> resolveReadBy(Message message) {
        if (message == null || message.getConversationId() == null || message.getConversationId().isBlank() || message.getPosition() == null) {
            return List.of();
        }

        return memberRepository.findByConversationIdAndLeftAtIsNull(message.getConversationId()).stream()
                .filter(member -> member.getLastReadPosition() != null && member.getLastReadPosition() >= message.getPosition())
                .filter(member -> message.getUser() == null || !member.getUserId().equals(message.getUser().getUserId()))
                .sorted(Comparator.comparing(Member::getLastReadAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(member -> MessageReadByResponse.builder()
                        .userId(member.getUserId())
                        .displayName(resolveDisplayName(member))
                        .avatarUrl(member.getAvatarUrl())
                        .readAt(member.getLastReadAt())
                        .build())
                .toList();
    }

    private Poll resolvePoll(Message message, String requesterId) {
        Poll stored = message.getPoll();
        if (stored == null) {
            return null;
        }

        Poll cloned = new Poll();
        cloned.setQuestion(stored.getQuestion());
        cloned.setAnswers(stored.getAnswers() == null ? List.of() : new ArrayList<>(stored.getAnswers()));
        cloned.setExpiry(stored.getExpiry());
        cloned.setAllowMultiSelect(stored.isAllowMultiSelect());

        if (stored.getResults() != null) {
            PollResults results = new PollResults();
            results.setFinalized(stored.getResults().isFinalized());
            results.setAnswerCounts(stored.getResults().getAnswerCounts() == null ? List.of() : new ArrayList<>(stored.getResults().getAnswerCounts()));
            if (requesterId != null && !requesterId.isBlank()) {
                results.setSelectedAnswerIds(pollVoteRepository.findByMessageIdAndUserId(message.getId(), requesterId).stream()
                        .map(com.fit.se.poll.domain.PollVote::getAnswerId)
                        .distinct()
                        .sorted()
                        .toList());
            }
            cloned.setResults(results);
        }
        return cloned;
    }
}

package com.fit.se.conversation.service;

import com.fit.se.conversation.dto.ConversationSummaryResponse;
import com.fit.se.conversation.dto.AddGroupMembersRequest;
import com.fit.se.conversation.dto.CreateGroupConversationRequest;
import com.fit.se.conversation.dto.DissolveGroupResponse;
import com.fit.se.conversation.dto.LeaveGroupResponse;
import com.fit.se.conversation.dto.RemoveGroupMemberResponse;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleRequest;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleResponse;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.conversation.dto.TypingIndicatorResponse;
import com.fit.se.conversation.dto.TransferGroupOwnerRequest;
import com.fit.se.conversation.dto.TransferGroupOwnerResponse;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.conversation.domain.ConversationStatus;
import com.fit.se.conversation.domain.ConversationType;
import com.fit.se.conversation.domain.Member;
import com.fit.se.conversation.domain.MemberRole;
import com.fit.se.message.domain.Message;
import com.fit.se.message.domain.MessageType;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.message.repository.MessageHiddenRepository;
import com.fit.se.message.domain.Sticker;
import com.fit.se.event.domain.ConsumedEventRepository;
import com.fit.se.conversation.repository.ConversationRepository;
import com.fit.se.conversation.repository.MemberRepository;
import com.fit.se.message.repository.MessageReactionRepository;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.poll.repository.PollVoteRepository;
import com.fit.se.user.service.UserDirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageReactionRepository messageReactionRepository;

    @Mock
    private MessageHiddenRepository messageHiddenRepository;

    @Mock
    private PollVoteRepository pollVoteRepository;

    @Mock
    private ConsumedEventRepository consumedEventRepository;

    @Mock
    private UserDirectoryService userDirectoryService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        conversationService = new ConversationService(
                conversationRepository,
                memberRepository,
                messageRepository,
                messageHiddenRepository,
                messageReactionRepository,
                pollVoteRepository,
                consumedEventRepository,
                userDirectoryService,
                messagingTemplate
        );
    }

    @Test
    void getUserConversations_buildsStickerPreviewForSidebar() {
        Member requesterMembership = membership("conv-1", "user-1", "User 1");
        Member counterpartMembership = membership("conv-1", "user-2", "User 2");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setLastMessageId("msg-9");
        conversation.setLastMessageAt(Instant.parse("2026-05-30T10:00:00Z"));
        conversation.setLastMessageId("msg-1");
        conversation.setLastMessageAt(Instant.parse("2026-05-30T09:10:00Z"));

        Message stickerMessage = new Message();
        stickerMessage.setId("msg-1");
        stickerMessage.setConversationId("conv-1");
        stickerMessage.setType(MessageType.DEFAULT);
        stickerMessage.setTimeStamp(Instant.parse("2026-05-30T09:10:00Z"));
        stickerMessage.setStickers(List.of(new Sticker("sticker-1", "https://example/sticker.png")));

        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("conv-1"), ConversationStatus.ACTIVE))
                .thenReturn(List.of(conversation));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1"))
                .thenReturn(List.of(requesterMembership, counterpartMembership));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of());
        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(stickerMessage));
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 0L))
                .thenReturn(List.of());

        List<ConversationSummaryResponse> responses = conversationService.getUserConversations("user-1");

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getLastMessage()).isEqualTo("Sticker");
        assertThat(responses.getFirst().getLastMessagePreview()).isEqualTo("Sticker");
    }

    @Test
    void sendTypingIndicator_includesDisplayName() {
        Member requesterMembership = membership("conv-1", "user-1", "User 1");
        UserProfileDocument profile = new UserProfileDocument();
        profile.setId("user-1");
        profile.setDisplayName("Nguyen Van A");

        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("conv-1", "user-1"))
                .thenReturn(Optional.of(requesterMembership));
        when(userDirectoryService.getOrCreate("user-1")).thenReturn(profile);

        conversationService.sendTypingIndicator("conv-1", "user-1", true);

        ArgumentCaptor<TypingIndicatorResponse> payloadCaptor = ArgumentCaptor.forClass(TypingIndicatorResponse.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/typing/conv-1"), payloadCaptor.capture());
        assertThat(payloadCaptor.getValue().getDisplayName()).isEqualTo("Nguyen Van A");
    }

    @Test
    void publishMessageEvent_sendsSingleThreadEvent() {
        Member user1Membership = membership("conv-1", "user-1", "User 1");
        Member user2Membership = membership("conv-1", "user-2", "User 2");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setLastMessageId("msg-1");
        conversation.setLastMessageAt(Instant.parse("2026-05-30T09:10:00Z"));

        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");
        message.setType(MessageType.DEFAULT);
        message.setContent("Xin chao");
        message.setTimeStamp(Instant.parse("2026-05-30T09:10:00Z"));

        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1"))
                .thenReturn(List.of(user1Membership, user2Membership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(user1Membership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-2")).thenReturn(List.of(user2Membership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("conv-1"), ConversationStatus.ACTIVE))
                .thenReturn(List.of(conversation));
        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of());
        when(messageHiddenRepository.findByUserIdAndConversationId("user-2", "conv-1")).thenReturn(List.of());
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 0L))
                .thenReturn(List.of());

        conversationService.publishMessageEvent(conversation, message);

        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/conversations/conv-1"), any(MessageResponse.class));
    }

    @Test
    void ensureDirectConversation_rejectsUnknownUserProfile() {
        when(userDirectoryService.getRequired("user-1")).thenThrow(new ResourceNotFoundException("Khong tim thay user profile: user-1"));

        assertThatThrownBy(() -> conversationService.ensureDirectConversation("user-1", "user-2"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("user-1");
    }

    @Test
    void markRead_updatesMembershipToLastMessagePosition() {
        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member membership = membership("conv-1", "user-1", "User 1");

        Message lastMessage = new Message();
        lastMessage.setId("msg-9");
        lastMessage.setConversationId("conv-1");
        lastMessage.setPosition(9L);
        lastMessage.setType(MessageType.DEFAULT);
        lastMessage.setTimeStamp(Instant.parse("2026-05-30T10:00:00Z"));

        when(conversationRepository.findByIdAndStatus("conv-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("conv-1", "user-1")).thenReturn(Optional.of(membership));
        when(messageRepository.findTopByConversationIdOrderByPositionDesc("conv-1")).thenReturn(Optional.of(lastMessage));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1")).thenReturn(List.of(membership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(membership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("conv-1"), ConversationStatus.ACTIVE))
                .thenReturn(List.of(conversation));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of());
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 9L)).thenReturn(List.of());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        conversationService.markRead("conv-1", "user-1");

        assertThat(membership.getLastReadPosition()).isEqualTo(9L);
        assertThat(membership.getLastReadMessageId()).isEqualTo("msg-9");
        assertThat(membership.getLastReadAt()).isNotNull();
        verify(messagingTemplate).convertAndSend(eq("/topic/conversations/conv-1"), any(MessageResponse.class));
    }

    @Test
    void getUserConversations_buildsRevokedPreviewForSidebar() {
        Member requesterMembership = membership("conv-1", "user-1", "User 1");
        Member counterpartMembership = membership("conv-1", "user-2", "User 2");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setLastMessageId("msg-1");
        conversation.setLastMessageAt(Instant.parse("2026-05-30T09:10:00Z"));

        Message revokedMessage = new Message();
        revokedMessage.setId("msg-1");
        revokedMessage.setConversationId("conv-1");
        revokedMessage.setType(MessageType.DEFAULT);
        revokedMessage.setDeletedTimeStamp(Instant.parse("2026-05-30T09:20:00Z"));

        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("conv-1"), ConversationStatus.ACTIVE))
                .thenReturn(List.of(conversation));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1"))
                .thenReturn(List.of(requesterMembership, counterpartMembership));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of());
        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(revokedMessage));
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 0L))
                .thenReturn(List.of());

        List<ConversationSummaryResponse> responses = conversationService.getUserConversations("user-1");

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getLastMessage()).isEqualTo("Tin nhan da duoc thu hoi");
    }

    @Test
    void getUserConversations_buildsDeletedForEveryonePreviewForSidebar() {
        Member requesterMembership = membership("conv-1", "user-1", "User 1");
        Member counterpartMembership = membership("conv-1", "user-2", "User 2");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setLastMessageId("msg-1");
        conversation.setLastMessageAt(Instant.parse("2026-05-30T09:10:00Z"));

        Message deletedMessage = new Message();
        deletedMessage.setId("msg-1");
        deletedMessage.setConversationId("conv-1");
        deletedMessage.setType(MessageType.DEFAULT);
        deletedMessage.setDeletedTimeStamp(Instant.parse("2026-05-30T09:20:00Z"));
        deletedMessage.setDeletedForEveryone(true);

        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("conv-1"), ConversationStatus.ACTIVE))
                .thenReturn(List.of(conversation));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1"))
                .thenReturn(List.of(requesterMembership, counterpartMembership));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of());
        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(deletedMessage));
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 0L))
                .thenReturn(List.of());

        List<ConversationSummaryResponse> responses = conversationService.getUserConversations("user-1");

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getLastMessage()).isEqualTo("Tin nhan da bi xoa");
    }

    @Test
    void createGroupConversation_createsOwnerAndGroupSummary() {
        CreateGroupConversationRequest request = new CreateGroupConversationRequest();
        request.setName("Nhom hoc tap");
        request.setParticipantIds(List.of("user-2", "user-3"));
        request.setAvatarUrl("https://example/group.png");

        UserProfileDocument ownerProfile = new UserProfileDocument();
        ownerProfile.setId("user-1");
        ownerProfile.setDisplayName("Owner");
        UserProfileDocument memberProfile = new UserProfileDocument();
        memberProfile.setId("user-2");
        memberProfile.setDisplayName("Member");
        UserProfileDocument memberProfile2 = new UserProfileDocument();
        memberProfile2.setId("user-3");
        memberProfile2.setDisplayName("Member 2");

        when(userDirectoryService.getRequired("user-1")).thenReturn(ownerProfile);
        when(userDirectoryService.getRequired("user-2")).thenReturn(memberProfile);
        when(userDirectoryService.getRequired("user-3")).thenReturn(memberProfile2);
        when(userDirectoryService.getOrCreate("user-1")).thenReturn(ownerProfile);
        when(userDirectoryService.getOrCreate("user-2")).thenReturn(memberProfile);
        when(userDirectoryService.getOrCreate("user-3")).thenReturn(memberProfile2);
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation saved = invocation.getArgument(0);
            saved.setId("group-1");
            return saved;
        });
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1"))
                .thenReturn(Optional.of(membership("group-1", "user-1", "Owner")));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1"))
                .thenReturn(List.of(
                        membership("group-1", "user-1", "Owner"),
                        membership("group-1", "user-2", "Member"),
                        membership("group-1", "user-3", "Member 2")
                ));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(membership("group-1", "user-1", "Owner")));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-2")).thenReturn(List.of(membership("group-1", "user-2", "Member")));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-3")).thenReturn(List.of(membership("group-1", "user-3", "Member 2")));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("group-1"), ConversationStatus.ACTIVE))
                .thenReturn(List.of(new Conversation("group-1", ConversationType.GROUP, "Nhom hoc tap", "https://example/group.png", "user-1", null, null, null, null, Instant.now(), Instant.now(), ConversationStatus.ACTIVE)));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "group-1")).thenReturn(List.of());
        when(messageHiddenRepository.findByUserIdAndConversationId("user-2", "group-1")).thenReturn(List.of());
        when(messageHiddenRepository.findByUserIdAndConversationId("user-3", "group-1")).thenReturn(List.of());

        ConversationSummaryResponse response = conversationService.createGroupConversation("user-1", request);

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getType()).isEqualTo("GROUP");
        assertThat(response.getGroupName()).isEqualTo("Nhom hoc tap");
        assertThat(response.getCounterpartId()).isNull();
    }

    @Test
    void createGroupConversation_rejectsWhenTotalParticipantsLessThanThree() {
        CreateGroupConversationRequest request = new CreateGroupConversationRequest();
        request.setName("Nhom nho");
        request.setParticipantIds(List.of("user-2"));

        assertThatThrownBy(() -> conversationService.createGroupConversation("user-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("it nhat 3 thanh vien");
    }

    @Test
    void getConversationDetail_returnsGroupMetadataAndActiveMembers() {
        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);
        requesterMembership.setJoinedAt(Instant.parse("2026-05-30T08:00:00Z"));

        Member adminMembership = membership("group-1", "user-2", "Admin");
        adminMembership.setRole(MemberRole.ADMIN);
        adminMembership.setJoinedAt(Instant.parse("2026-05-30T08:05:00Z"));

        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setName("Nhom hoc tap");
        conversation.setAvatarUrl("https://example/group.png");
        conversation.setOwnerId("user-1");
        conversation.setDescription("Mo ta nhom");
        conversation.setCreatedAt(Instant.parse("2026-05-30T07:55:00Z"));
        conversation.setStatus(ConversationStatus.ACTIVE);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of(requesterMembership, adminMembership));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "group-1")).thenReturn(List.of());
        when(messageRepository.findByConversationIdOrderByPositionAsc("group-1")).thenReturn(List.of());
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("group-1", 0L)).thenReturn(List.of());

        var detail = conversationService.getConversationDetail("group-1", "user-1");

        assertThat(detail.getType()).isEqualTo("GROUP");
        assertThat(detail.getGroupName()).isEqualTo("Nhom hoc tap");
        assertThat(detail.getOwnerId()).isEqualTo("user-1");
        assertThat(detail.getDescription()).isEqualTo("Mo ta nhom");
        assertThat(detail.getActiveMemberCount()).isEqualTo(2);
        assertThat(detail.getMyRole()).isEqualTo("OWNER");
        assertThat(detail.getBlocked()).isFalse();
        assertThat(detail.getMembers()).hasSize(2);
        assertThat(detail.getMembers().getFirst().getUserId()).isEqualTo("user-1");
        assertThat(detail.getMembers().getFirst().getRole()).isEqualTo("OWNER");
        assertThat(detail.getMembers().getFirst().getActive()).isTrue();
    }

    @Test
    void getConversationDetail_hidesMessagesDeletedForCurrentUser() {
        Member requesterMembership = membership("conv-1", "user-1", "User 1");
        Member counterpartMembership = membership("conv-1", "user-2", "User 2");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Message hiddenMessage = new Message();
        hiddenMessage.setId("msg-hidden");
        hiddenMessage.setConversationId("conv-1");
        hiddenMessage.setPosition(1L);
        hiddenMessage.setType(MessageType.DEFAULT);

        Message visibleMessage = new Message();
        visibleMessage.setId("msg-visible");
        visibleMessage.setConversationId("conv-1");
        visibleMessage.setPosition(2L);
        visibleMessage.setType(MessageType.DEFAULT);

        com.fit.se.message.domain.MessageHidden hidden = new com.fit.se.message.domain.MessageHidden();
        hidden.setMessageId("msg-hidden");
        hidden.setConversationId("conv-1");
        hidden.setUserId("user-1");

        when(conversationRepository.findByIdAndStatus("conv-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("conv-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1")).thenReturn(List.of(requesterMembership, counterpartMembership));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of(hidden));
        when(messageRepository.findByConversationIdOrderByPositionAsc("conv-1")).thenReturn(List.of(hiddenMessage, visibleMessage));
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 0L)).thenReturn(List.of(hiddenMessage, visibleMessage));

        var detail = conversationService.getConversationDetail("conv-1", "user-1");

        assertThat(detail.getMessages()).hasSize(1);
        assertThat(detail.getMessages().getFirst().getId()).isEqualTo("msg-visible");
    }

    @Test
    void getConversationDetail_includesReadByForMessage() {
        Member requesterMembership = membership("conv-1", "user-1", "User 1");
        requesterMembership.setLastReadPosition(0L);
        Member readerMembership = membership("conv-1", "user-2", "User 2");
        readerMembership.setLastReadPosition(2L);
        readerMembership.setLastReadAt(Instant.parse("2026-05-30T10:05:00Z"));
        readerMembership.setAvatarUrl("https://example/avatar.png");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setType(ConversationType.DIRECT);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Message visibleMessage = new Message();
        visibleMessage.setId("msg-visible");
        visibleMessage.setConversationId("conv-1");
        visibleMessage.setPosition(2L);
        visibleMessage.setType(MessageType.DEFAULT);
        visibleMessage.setUser(new com.fit.se.user.domain.User("user-1", "User 1", null));

        when(conversationRepository.findByIdAndStatus("conv-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("conv-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("conv-1")).thenReturn(List.of(requesterMembership, readerMembership));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "conv-1")).thenReturn(List.of());
        when(messageRepository.findByConversationIdOrderByPositionAsc("conv-1")).thenReturn(List.of(visibleMessage));
        when(messageRepository.findByConversationIdAndPositionGreaterThanOrderByPositionAsc("conv-1", 0L)).thenReturn(List.of(visibleMessage));

        var detail = conversationService.getConversationDetail("conv-1", "user-1");

        assertThat(detail.getMessages()).hasSize(1);
        assertThat(detail.getMessages().getFirst().getReadBy()).hasSize(1);
        assertThat(detail.getMessages().getFirst().getReadBy().getFirst().getUserId()).isEqualTo("user-2");
    }

    @Test
    void addGroupMembers_requiresOwnerOrAdmin() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "User 1");
        requesterMembership.setRole(MemberRole.MEMBER);

        AddGroupMembersRequest request = new AddGroupMembersRequest();
        request.setParticipantIds(List.of("user-3"));

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));

        assertThatThrownBy(() -> conversationService.addGroupMembers("group-1", "user-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("khong co quyen");
    }

    @Test
    void addGroupMembers_addsNewMemberAndReturnsGroupSummary() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setName("Nhom hoc tap");
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);

        UserProfileDocument newMemberProfile = new UserProfileDocument();
        newMemberProfile.setId("user-3");
        newMemberProfile.setDisplayName("User 3");

        AddGroupMembersRequest request = new AddGroupMembersRequest();
        request.setParticipantIds(List.of("user-3"));

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-3")).thenReturn(Optional.empty());
        when(userDirectoryService.getRequired("user-3")).thenReturn(newMemberProfile);
        when(userDirectoryService.getOrCreate("user-3")).thenReturn(newMemberProfile);
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of(requesterMembership, membership("group-1", "user-3", "User 3")));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-3")).thenReturn(List.of(membership("group-1", "user-3", "User 3")));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("group-1"), ConversationStatus.ACTIVE)).thenReturn(List.of(conversation));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "group-1")).thenReturn(List.of());
        when(messageHiddenRepository.findByUserIdAndConversationId("user-3", "group-1")).thenReturn(List.of());

        ConversationSummaryResponse response = conversationService.addGroupMembers("group-1", "user-1", request);

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getType()).isEqualTo("GROUP");
        assertThat(response.getGroupName()).isEqualTo("Nhom hoc tap");
    }

    @Test
    void removeGroupMember_adminCannotRemoveAdmin() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Admin");
        requesterMembership.setRole(MemberRole.ADMIN);
        Member targetMembership = membership("group-1", "user-2", "Admin 2");
        targetMembership.setRole(MemberRole.ADMIN);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-2")).thenReturn(Optional.of(targetMembership));

        assertThatThrownBy(() -> conversationService.removeGroupMember("group-1", "user-1", "user-2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Admin chi duoc xoa member");
    }

    @Test
    void removeGroupMember_ownerRemovesMember() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);
        Member targetMembership = membership("group-1", "user-2", "Member");
        targetMembership.setRole(MemberRole.MEMBER);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-2")).thenReturn(Optional.of(targetMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of(requesterMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("group-1"), ConversationStatus.ACTIVE)).thenReturn(List.of(conversation));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "group-1")).thenReturn(List.of());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RemoveGroupMemberResponse response = conversationService.removeGroupMember("group-1", "user-1", "user-2");

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getRemovedUserId()).isEqualTo("user-2");
        assertThat(response.getRemoved()).isTrue();
        assertThat(targetMembership.getLeftAt()).isNotNull();
    }

    @Test
    void updateGroupMemberRole_requiresOwner() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Admin");
        requesterMembership.setRole(MemberRole.ADMIN);

        UpdateGroupMemberRoleRequest request = new UpdateGroupMemberRoleRequest();
        request.setRole("ADMIN");

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));

        assertThatThrownBy(() -> conversationService.updateGroupMemberRole("group-1", "user-1", "user-2", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("owner");
    }

    @Test
    void updateGroupMemberRole_ownerPromotesMemberToAdmin() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);
        Member targetMembership = membership("group-1", "user-2", "Member");
        targetMembership.setRole(MemberRole.MEMBER);

        UpdateGroupMemberRoleRequest request = new UpdateGroupMemberRoleRequest();
        request.setRole("ADMIN");

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-2")).thenReturn(Optional.of(targetMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of(requesterMembership, targetMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-2")).thenReturn(List.of(targetMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("group-1"), ConversationStatus.ACTIVE)).thenReturn(List.of(conversation));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "group-1")).thenReturn(List.of());
        when(messageHiddenRepository.findByUserIdAndConversationId("user-2", "group-1")).thenReturn(List.of());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateGroupMemberRoleResponse response = conversationService.updateGroupMemberRole("group-1", "user-1", "user-2", request);

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getUserId()).isEqualTo("user-2");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(targetMembership.getRole()).isEqualTo(MemberRole.ADMIN);
    }

    @Test
    void transferGroupOwner_requiresCurrentOwner() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Admin");
        requesterMembership.setRole(MemberRole.ADMIN);

        TransferGroupOwnerRequest request = new TransferGroupOwnerRequest();
        request.setNewOwnerUserId("user-2");

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));

        assertThatThrownBy(() -> conversationService.transferGroupOwner("group-1", "user-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("owner");
    }

    @Test
    void transferGroupOwner_demotesOldOwnerAndPromotesNewOwner() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setOwnerId("user-1");

        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);
        Member targetMembership = membership("group-1", "user-2", "Admin");
        targetMembership.setRole(MemberRole.ADMIN);

        TransferGroupOwnerRequest request = new TransferGroupOwnerRequest();
        request.setNewOwnerUserId("user-2");

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-2")).thenReturn(Optional.of(targetMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of(requesterMembership, targetMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-2")).thenReturn(List.of(targetMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("group-1"), ConversationStatus.ACTIVE)).thenReturn(List.of(conversation));
        when(messageHiddenRepository.findByUserIdAndConversationId("user-1", "group-1")).thenReturn(List.of());
        when(messageHiddenRepository.findByUserIdAndConversationId("user-2", "group-1")).thenReturn(List.of());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferGroupOwnerResponse response = conversationService.transferGroupOwner("group-1", "user-1", request);

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getPreviousOwnerUserId()).isEqualTo("user-1");
        assertThat(response.getNewOwnerUserId()).isEqualTo("user-2");
        assertThat(response.getTransferred()).isTrue();
        assertThat(requesterMembership.getRole()).isEqualTo(MemberRole.ADMIN);
        assertThat(targetMembership.getRole()).isEqualTo(MemberRole.OWNER);
        assertThat(conversation.getOwnerId()).isEqualTo("user-2");
    }

    @Test
    void leaveGroup_ownerIsRejected() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));

        assertThatThrownBy(() -> conversationService.leaveGroup("group-1", "user-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Owner");
    }

    @Test
    void leaveGroup_memberLeavesSuccessfully() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-2", "Member");
        requesterMembership.setRole(MemberRole.MEMBER);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-2")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveGroupResponse response = conversationService.leaveGroup("group-1", "user-2");

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getUserId()).isEqualTo("user-2");
        assertThat(response.getLeft()).isTrue();
        assertThat(requesterMembership.getLeftAt()).isNotNull();
    }

    @Test
    void dissolveGroup_requiresOwner() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-2", "Member");
        requesterMembership.setRole(MemberRole.MEMBER);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-2")).thenReturn(Optional.of(requesterMembership));

        assertThatThrownBy(() -> conversationService.dissolveGroup("group-1", "user-2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("owner");
    }

    @Test
    void dissolveGroup_ownerArchivesConversation() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");
        conversation.setType(ConversationType.GROUP);
        conversation.setStatus(ConversationStatus.ACTIVE);

        Member requesterMembership = membership("group-1", "user-1", "Owner");
        requesterMembership.setRole(MemberRole.OWNER);

        when(conversationRepository.findByIdAndStatus("group-1", ConversationStatus.ACTIVE)).thenReturn(Optional.of(conversation));
        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("group-1", "user-1")).thenReturn(Optional.of(requesterMembership));
        when(memberRepository.findByConversationIdAndLeftAtIsNull("group-1")).thenReturn(List.of(requesterMembership));
        when(memberRepository.findByUserIdAndLeftAtIsNull("user-1")).thenReturn(List.of(requesterMembership));
        when(conversationRepository.findByIdInAndStatusOrderByLastMessageAtDesc(List.of("group-1"), ConversationStatus.ACTIVE)).thenReturn(List.of());
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DissolveGroupResponse response = conversationService.dissolveGroup("group-1", "user-1");

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getDissolved()).isTrue();
        assertThat(conversation.getStatus()).isEqualTo(ConversationStatus.DELETE);
    }

    private Member membership(String conversationId, String userId, String nick) {
        Member member = new Member();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setNick(nick);
        member.setLastReadPosition(0L);
        return member;
    }
}

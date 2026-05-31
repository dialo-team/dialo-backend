package com.fit.se.message.service;

import com.fit.se.message.dto.DeleteMessageResponse;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.message.dto.SendDirectMessageRequest;
import com.fit.se.message.dto.SendMessageRequest;
import com.fit.se.message.dto.ForwardMessageRequest;
import com.fit.se.message.dto.EditMessageRequest;
import com.fit.se.user.domain.User;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.message.domain.MessageHidden;
import com.fit.se.message.domain.Message;
import com.fit.se.message.domain.MessageReference;
import com.fit.se.message.domain.Reaction;
import com.fit.se.message.domain.MessageType;
import com.fit.se.message.domain.Sticker;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.repository.MessageHiddenRepository;
import com.fit.se.message.repository.MessageReactionRepository;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.user.service.UserDirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageHiddenRepository messageHiddenRepository;

    @Mock
    private MessageReactionRepository messageReactionRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserDirectoryService userDirectoryService;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, messageHiddenRepository, messageReactionRepository, conversationService, userDirectoryService);
    }

    @Test
    void sendDirectMessage_requiresExistingTargetProfile() {
        SendDirectMessageRequest request = new SendDirectMessageRequest();
        request.setTargetUserId("user-2");
        request.setContent("Xin chao");

        UserProfileDocument senderProfile = profile("user-1", "User 1");
        when(userDirectoryService.getRequired("user-1")).thenReturn(senderProfile);
        when(userDirectoryService.getRequired("user-2"))
                .thenThrow(new ResourceNotFoundException("Khong tim thay user profile: user-2"));

        assertThatThrownBy(() -> messageService.sendDirectMessage("user-1", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("user-2");
    }

    @Test
    void sendMessage_normalizesReferenceToCurrentConversation() {
        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId("conv-1");
        request.setContent("Reply");
        request.setReference(new MessageReference("msg-1", "conv-other", true));

        Message referencedMessage = new Message();
        referencedMessage.setId("msg-1");
        referencedMessage.setConversationId("conv-1");

        UserProfileDocument senderProfile = profile("user-1", "User 1");

        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(referencedMessage));
        when(userDirectoryService.getRequired("user-1")).thenReturn(senderProfile);
        when(conversationService.nextPosition("conv-1")).thenReturn(1L);
        when(conversationService.toMessageResponse(any(Message.class), any())).thenReturn(null);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(messageRepository.save(messageCaptor.capture())).thenAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            saved.setId("saved-1");
            saved.setType(MessageType.DEFAULT);
            saved.setTimeStamp(Instant.parse("2026-05-30T10:00:00Z"));
            return saved;
        });

        messageService.sendMessage("user-1", request);

        assertThat(messageCaptor.getValue().getReference()).isNotNull();
        assertThat(messageCaptor.getValue().getReference().getConversationId()).isEqualTo("conv-1");
        assertThat(messageCaptor.getValue().getReference().isExist()).isTrue();
    }

    @Test
    void sendMessage_supportsGroupConversationForActiveMember() {
        Conversation conversation = new Conversation();
        conversation.setId("group-1");

        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId("group-1");
        request.setContent("Chao ca nhom");

        UserProfileDocument senderProfile = profile("user-1", "User 1");

        when(conversationService.getConversationEntity("group-1")).thenReturn(conversation);
        when(conversationService.getActiveMembership("group-1", "user-1")).thenReturn(null);
        when(userDirectoryService.getRequired("user-1")).thenReturn(senderProfile);
        when(conversationService.nextPosition("group-1")).thenReturn(5L);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            saved.setId("msg-group-1");
            saved.setType(MessageType.DEFAULT);
            saved.setTimeStamp(Instant.parse("2026-05-30T10:00:00Z"));
            return saved;
        });
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-group-1")
                .conversationId("group-1")
                .content("Chao ca nhom")
                .build());

        MessageResponse response = messageService.sendMessage("user-1", request);

        assertThat(response.getConversationId()).isEqualTo("group-1");
        assertThat(response.getContent()).isEqualTo("Chao ca nhom");
    }

    @Test
    void sendMessage_normalizesMentionsFromStoredProfiles() {
        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId("conv-1");
        request.setContent("Hello @user-2");
        request.setMentions(List.of(new User("user-2", "client-value", null)));

        UserProfileDocument senderProfile = profile("user-1", "User 1");
        UserProfileDocument mentionedProfile = profile("user-2", "Nguyen Van A");

        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(conversationService.getActiveMembership("conv-1", "user-2")).thenReturn(null);
        when(userDirectoryService.getRequired("user-1")).thenReturn(senderProfile);
        when(userDirectoryService.getRequired("user-2")).thenReturn(mentionedProfile);
        when(conversationService.nextPosition("conv-1")).thenReturn(1L);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(messageRepository.save(messageCaptor.capture())).thenAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            saved.setId("saved-1");
            saved.setType(MessageType.DEFAULT);
            saved.setTimeStamp(Instant.parse("2026-05-30T10:00:00Z"));
            return saved;
        });
        when(conversationService.toMessageResponse(any(Message.class), any())).thenReturn(null);

        messageService.sendMessage("user-1", request);

        assertThat(messageCaptor.getValue().getMentions()).hasSize(1);
        assertThat(messageCaptor.getValue().getMentions().getFirst().getNick()).isEqualTo("Nguyen Van A");
    }

    @Test
    void sendMessage_rejectsMentionOutsideConversation() {
        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId("conv-1");
        request.setContent("Hello");
        request.setMentions(List.of(new User("user-3", "User 3", null)));

        UserProfileDocument senderProfile = profile("user-1", "User 1");

        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(conversationService.getActiveMembership("conv-1", "user-3"))
                .thenThrow(new ResourceNotFoundException("Nguoi dung khong nam trong conversation"));

        assertThatThrownBy(() -> messageService.sendMessage("user-1", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("khong nam trong conversation");
    }

    @Test
    void addReaction_persistsReactionAndReturnsUpdatedMessage() {
        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageReactionRepository.findByMessageIdAndUserIdAndEmoji("msg-1", "user-1", 128077)).thenReturn(Optional.empty());
        when(conversationService.toMessageResponse(message, null)).thenReturn(MessageResponse.builder()
                .reactions(List.of(new Reaction(1, 128077, false)))
                .build());
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-1")
                .reactions(List.of(new Reaction(1, 128077, true)))
                .build());

        MessageResponse response = messageService.addReaction("user-1", "msg-1", 128077);

        assertThat(response.getReactions()).hasSize(1);
        assertThat(response.getReactions().getFirst().getEmoji()).isEqualTo(new String(Character.toChars(128077)));
        assertThat(response.getReactions().getFirst().getEmojiCode()).isEqualTo(128077);
        assertThat(response.getReactions().getFirst().isMe()).isTrue();
    }

    @Test
    void pinMessage_marksMessagePinned() {
        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");
        message.setPinned(false);

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-1")
                .pinned(true)
                .build());

        MessageResponse response = messageService.pinMessage("user-1", "msg-1");

        assertThat(response.getPinned()).isTrue();
        assertThat(message.getPinned()).isTrue();
    }

    @Test
    void pinMessage_rejectsWhenConversationAlreadyHasThreePinnedMessages() {
        Message message = new Message();
        message.setId("msg-4");
        message.setConversationId("conv-1");
        message.setPinned(false);

        Message pinned1 = new Message();
        pinned1.setPinned(true);
        Message pinned2 = new Message();
        pinned2.setPinned(true);
        Message pinned3 = new Message();
        pinned3.setPinned(true);

        when(messageRepository.findById("msg-4")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageRepository.findByConversationIdOrderByPositionAsc("conv-1")).thenReturn(List.of(pinned1, pinned2, pinned3, message));

        assertThatThrownBy(() -> messageService.pinMessage("user-1", "msg-4"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("toi da 3 tin nhan");
    }

    @Test
    void revokeMessage_replacesPayloadWithRevokedPlaceholder() {
        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");
        message.setType(MessageType.DEFAULT);
        message.setUser(new User("user-1", "User 1", null));
        message.setContent("Xin chao");
        message.setStickers(List.of(new Sticker("st-1", "https://example/sticker.png")));

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageReactionRepository.findByMessageId("msg-1")).thenReturn(List.of());
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-1")
                .content("Tin nhan da duoc thu hoi")
                .deleted(true)
                .build());

        MessageResponse response = messageService.revokeMessage("user-1", "msg-1");

        assertThat(response.getDeleted()).isTrue();
        assertThat(message.getContent()).isEqualTo("Tin nhan da duoc thu hoi");
        assertThat(message.getDeletedTimeStamp()).isNotNull();
        assertThat(message.getStickers()).isEmpty();
        assertThat(message.getSnapshots()).hasSize(1);
    }

    @Test
    void forwardMessage_toConversation_clonesPayloadIntoTargetConversation() {
        Message source = new Message();
        source.setId("msg-1");
        source.setConversationId("conv-source");
        source.setType(MessageType.DEFAULT);
        source.setUser(new User("user-2", "User 2", null));
        source.setContent("Xin chao");
        source.setAttachments(List.of());

        Conversation target = new Conversation();
        target.setId("conv-target");

        ForwardMessageRequest request = new ForwardMessageRequest();
        request.setConversationId("conv-target");

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(source));
        when(conversationService.getActiveMembership("conv-source", "user-1")).thenReturn(null);
        when(conversationService.getConversationEntity("conv-target")).thenReturn(target);
        when(conversationService.getActiveMembership("conv-target", "user-1")).thenReturn(null);
        when(userDirectoryService.getRequired("user-1")).thenReturn(profile("user-1", "User 1"));
        when(conversationService.nextPosition("conv-target")).thenReturn(3L);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            saved.setId("msg-forward");
            saved.setType(MessageType.DEFAULT);
            saved.setTimeStamp(Instant.parse("2026-05-30T10:00:00Z"));
            return saved;
        });
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-forward")
                .conversationId("conv-target")
                .content("Xin chao")
                .build());

        MessageResponse response = messageService.forwardMessage("user-1", "msg-1", request);

        assertThat(response.getConversationId()).isEqualTo("conv-target");
        assertThat(response.getContent()).isEqualTo("Xin chao");
    }

    @Test
    void deleteMessageForMe_createsHiddenMarkerAndReturnsAck() {
        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");
        message.setPosition(7L);

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageHiddenRepository.existsByUserIdAndMessageId("user-1", "msg-1")).thenReturn(false);

        DeleteMessageResponse response = messageService.deleteMessageForMe("user-1", "msg-1");

        assertThat(response.getDeletedForMe()).isTrue();
        assertThat(response.getConversationId()).isEqualTo("conv-1");
        assertThat(response.getMessageId()).isEqualTo("msg-1");
    }

    @Test
    void deleteMessageForEveryone_replacesPayloadWithDeletedPlaceholder() {
        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");
        message.setType(MessageType.DEFAULT);
        message.setUser(new User("user-1", "User 1", null));
        message.setContent("Xin chao");
        message.setPinned(true);
        message.setStickers(List.of(new Sticker("st-1", "https://example/sticker.png")));

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageReactionRepository.findByMessageId("msg-1")).thenReturn(List.of());
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-1")
                .content("Tin nhan da bi xoa")
                .deleted(true)
                .deletedForEveryone(true)
                .build());

        MessageResponse response = messageService.deleteMessageForEveryone("user-1", "msg-1");

        assertThat(response.getDeleted()).isTrue();
        assertThat(response.getDeletedForEveryone()).isTrue();
        assertThat(message.getContent()).isEqualTo("Tin nhan da bi xoa");
        assertThat(message.getDeletedTimeStamp()).isNotNull();
        assertThat(message.getDeletedForEveryone()).isTrue();
        assertThat(message.getPinned()).isFalse();
        assertThat(message.getStickers()).isEmpty();
        assertThat(message.getSnapshots()).hasSize(1);
    }

    @Test
    void editMessage_updatesContentAndEditedTimestamp() {
        Message message = new Message();
        message.setId("msg-1");
        message.setConversationId("conv-1");
        message.setType(MessageType.DEFAULT);
        message.setUser(new User("user-1", "User 1", null));
        message.setContent("Cu");

        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        EditMessageRequest request = new EditMessageRequest();
        request.setContent("Moi");

        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(null);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-1")
                .content("Moi")
                .editedTimeStamp(Instant.parse("2026-05-30T10:00:00Z"))
                .build());

        MessageResponse response = messageService.editMessage("user-1", "msg-1", request);

        assertThat(response.getContent()).isEqualTo("Moi");
        assertThat(message.getContent()).isEqualTo("Moi");
        assertThat(message.getEditedTimeStamp()).isNotNull();
        assertThat(message.getSnapshots()).hasSize(1);
    }

    private UserProfileDocument profile(String id, String displayName) {
        UserProfileDocument profile = new UserProfileDocument();
        profile.setId(id);
        profile.setDisplayName(displayName);
        return profile;
    }
}

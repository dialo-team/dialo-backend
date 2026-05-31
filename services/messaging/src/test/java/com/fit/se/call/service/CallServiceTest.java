package com.fit.se.call.service;

import com.fit.se.call.dto.CreateCallRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.message.dto.SendMessageRequest;
import com.fit.se.user.domain.User;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.conversation.domain.Member;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.message.domain.Message;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.message.service.MessageService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private MessageService messageService;

    @Mock
    private UserDirectoryService userDirectoryService;

    private CallService callService;

    @BeforeEach
    void setUp() {
        callService = new CallService(messageRepository, conversationService, messageService, userDirectoryService);
    }

    @Test
    void createCall_buildsOngoingCallMessage() {
        Conversation conversation = new Conversation();
        conversation.setId("conv-1");

        CreateCallRequest request = new CreateCallRequest();
        request.setType("VIDEO");
        request.setUserIds(List.of("user-1", "user-2"));

        UserProfileDocument user1 = profile("user-1", "User 1");
        UserProfileDocument user2 = profile("user-2", "User 2");

        when(conversationService.getConversationEntity("conv-1")).thenReturn(conversation);
        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(new Member());
        when(conversationService.getActiveMembership("conv-1", "user-2")).thenReturn(new Member());
        when(userDirectoryService.getRequired("user-1")).thenReturn(user1);
        when(userDirectoryService.getRequired("user-2")).thenReturn(user2);
        when(messageService.sendMessage(eq("user-1"), any())).thenReturn(MessageResponse.builder().id("msg-call").build());

        ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);

        callService.createCall("conv-1", "user-1", request);

        verify(messageService).sendMessage(eq("user-1"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getCall()).isNotNull();
        assertThat(requestCaptor.getValue().getCall().getType()).isEqualTo("VIDEO");
        assertThat(requestCaptor.getValue().getCall().getStatus()).isEqualTo("ONGOING");
        assertThat(requestCaptor.getValue().getCall().getUsers()).hasSize(2);
    }

    @Test
    void endCall_marksCallEndedAndPublishesUpdate() {
        Message message = new Message();
        message.setId("msg-call");
        message.setConversationId("conv-1");
        message.setCall(new CallInfo("VIDEO", "ONGOING", List.of(new User("user-1", "User 1", null)), Instant.parse("2026-05-30T10:00:00Z"), null));

        when(conversationService.getActiveMembership("conv-1", "user-1")).thenReturn(new Member());
        when(messageRepository.findById("msg-call")).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1"))).thenReturn(MessageResponse.builder()
                .id("msg-call")
                .call(new CallInfo("VIDEO", "ENDED", List.of(new User("user-1", "User 1", null)), Instant.parse("2026-05-30T10:00:00Z"), Instant.parse("2026-05-30T10:30:00Z")))
                .build());

        MessageResponse response = callService.endCall("conv-1", "msg-call", "user-1");

        assertThat(response.getCall().getStatus()).isEqualTo("ENDED");
        assertThat(message.getCall().getEndedTime()).isNotNull();
        verify(conversationService).publishMessageUpdateEvent(message);
    }

    private UserProfileDocument profile(String id, String displayName) {
        UserProfileDocument profile = new UserProfileDocument();
        profile.setId(id);
        profile.setDisplayName(displayName);
        return profile;
    }
}

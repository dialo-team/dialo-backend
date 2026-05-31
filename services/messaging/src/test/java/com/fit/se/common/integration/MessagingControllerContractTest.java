package com.fit.se.common.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fit.se.common.exception.GlobalExceptionHandler;
import com.fit.se.attachment.dto.AttachmentUploadItemResponse;
import com.fit.se.attachment.dto.AttachmentUploadResponse;
import com.fit.se.attachment.controller.AttachmentController;
import com.fit.se.conversation.dto.ConversationSummaryResponse;
import com.fit.se.message.dto.DeleteMessageResponse;
import com.fit.se.conversation.dto.LeaveGroupResponse;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.conversation.dto.RemoveGroupMemberResponse;
import com.fit.se.conversation.dto.DissolveGroupResponse;
import com.fit.se.poll.dto.PollAnswerVotesResponse;
import com.fit.se.conversation.dto.UpdateGroupMemberRoleResponse;
import com.fit.se.conversation.dto.TransferGroupOwnerResponse;
import com.fit.se.call.controller.CallController;
import com.fit.se.conversation.controller.ConversationController;
import com.fit.se.message.domain.MessageReference;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.poll.domain.Poll;
import com.fit.se.poll.domain.PollAnswer;
import com.fit.se.poll.domain.PollAnswerCount;
import com.fit.se.poll.domain.PollResults;
import com.fit.se.message.domain.Reaction;
import com.fit.se.user.domain.User;
import com.fit.se.message.controller.MessageController;
import com.fit.se.poll.controller.PollController;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.attachment.service.AttachmentService;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.service.MessageService;
import com.fit.se.poll.service.PollService;
import com.fit.se.call.service.CallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessagingControllerContractTest {

    private final ConversationService conversationService = Mockito.mock(ConversationService.class);
    private final MessageService messageService = Mockito.mock(MessageService.class);
    private final AttachmentService attachmentService = Mockito.mock(AttachmentService.class);
    private final PollService pollService = Mockito.mock(PollService.class);
    private final CallService callService = Mockito.mock(CallService.class);

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ConversationController(conversationService),
                        new MessageController(messageService),
                        new AttachmentController(attachmentService),
                        new PollController(pollService),
                        new CallController(callService)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createConversation_returnsSummaryShape() throws Exception {
        ConversationSummaryResponse response = ConversationSummaryResponse.builder()
                .conversationId("conv-1")
                .type("DIRECT")
                .counterpartId("user-2")
                .counterpartName("Nguyen Van A")
                .counterpartAvatarUrl("https://example/avatar.png")
                .build();

        when(conversationService.createDirectConversation(eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participantIds": ["user-1", "user-2"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.type").value("DIRECT"))
                .andExpect(jsonPath("$.counterpartId").value("user-2"));
    }

    @Test
    void createGroupConversation_returnsGroupSummaryShape() throws Exception {
        ConversationSummaryResponse response = ConversationSummaryResponse.builder()
                .conversationId("group-1")
                .type("GROUP")
                .groupName("Nhom hoc tap")
                .groupAvatarUrl("https://example/group.png")
                .build();

        when(conversationService.createGroupConversation(eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/groups")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nhom hoc tap",
                                  "participantIds": ["user-2", "user-3"],
                                  "avatarUrl": "https://example/group.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.type").value("GROUP"))
                .andExpect(jsonPath("$.groupName").value("Nhom hoc tap"));
    }

    @Test
    void addGroupMembers_returnsGroupSummaryShape() throws Exception {
        ConversationSummaryResponse response = ConversationSummaryResponse.builder()
                .conversationId("group-1")
                .type("GROUP")
                .groupName("Nhom hoc tap")
                .build();

        when(conversationService.addGroupMembers(eq("group-1"), eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/group-1/members")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participantIds": ["user-3"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.type").value("GROUP"))
                .andExpect(jsonPath("$.groupName").value("Nhom hoc tap"));
    }

    @Test
    void removeGroupMember_returnsAckShape() throws Exception {
        RemoveGroupMemberResponse response = RemoveGroupMemberResponse.builder()
                .conversationId("group-1")
                .removedUserId("user-3")
                .removed(true)
                .build();

        when(conversationService.removeGroupMember("group-1", "user-1", "user-3")).thenReturn(response);

        mockMvc.perform(delete("/api/v1/conversations/group-1/members/user-3")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.removedUserId").value("user-3"))
                .andExpect(jsonPath("$.removed").value(true));
    }

    @Test
    void updateGroupMemberRole_returnsRoleShape() throws Exception {
        UpdateGroupMemberRoleResponse response = UpdateGroupMemberRoleResponse.builder()
                .conversationId("group-1")
                .userId("user-3")
                .role("ADMIN")
                .build();

        when(conversationService.updateGroupMemberRole(eq("group-1"), eq("user-1"), eq("user-3"), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/conversations/group-1/members/user-3/role")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.userId").value("user-3"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void transferGroupOwner_returnsAckShape() throws Exception {
        TransferGroupOwnerResponse response = TransferGroupOwnerResponse.builder()
                .conversationId("group-1")
                .previousOwnerUserId("user-1")
                .newOwnerUserId("user-3")
                .transferred(true)
                .build();

        when(conversationService.transferGroupOwner(eq("group-1"), eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/conversations/group-1/owner")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newOwnerUserId": "user-3"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.previousOwnerUserId").value("user-1"))
                .andExpect(jsonPath("$.newOwnerUserId").value("user-3"))
                .andExpect(jsonPath("$.transferred").value(true));
    }

    @Test
    void leaveGroup_returnsAckShape() throws Exception {
        LeaveGroupResponse response = LeaveGroupResponse.builder()
                .conversationId("group-1")
                .userId("user-2")
                .left(true)
                .build();

        when(conversationService.leaveGroup("group-1", "user-2")).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/group-1/leave")
                        .header("X-User-Id", "user-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.userId").value("user-2"))
                .andExpect(jsonPath("$.left").value(true));
    }

    @Test
    void dissolveGroup_returnsAckShape() throws Exception {
        DissolveGroupResponse response = DissolveGroupResponse.builder()
                .conversationId("group-1")
                .dissolved(true)
                .build();

        when(conversationService.dissolveGroup("group-1", "user-1")).thenReturn(response);

        mockMvc.perform(delete("/api/v1/conversations/group-1")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.dissolved").value(true));
    }

    @Test
    void createMessage_returnsMessageResponseShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-1")
                .conversationId("conv-1")
                .senderId("user-1")
                .senderName("User 1")
                .type("DEFAULT")
                .content("Xin chao")
                .readBy(List.of())
                .reference(new MessageReference("msg-0", "conv-1", true))
                .system(false)
                .position(2L)
                .createdAt(Instant.parse("2026-05-30T10:00:00Z"))
                .build();

        when(messageService.sendMessage(eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/messages")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId": "conv-1",
                                  "content": "Xin chao",
                                  "attachments": [],
                                  "stickers": [],
                                  "reference": {
                                    "messageId": "msg-0"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-1"))
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.type").value("DEFAULT"))
                .andExpect(jsonPath("$.readBy").isArray())
                .andExpect(jsonPath("$.reference.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.reference.exist").value(true));
    }

    @Test
    void uploadAttachment_returnsAttachmentShape() throws Exception {
        AttachmentUploadResponse response = AttachmentUploadResponse.builder()
                .items(List.of(AttachmentUploadItemResponse.builder()
                        .id("att-1")
                        .fileName("hello.txt")
                        .contentType("text/plain")
                        .size(11)
                        .url("https://cdn.example.com/messaging/attachments/att-1.txt")
                        .build()))
                .build();

        when(attachmentService.upload(any())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("files", "hello.txt", "text/plain", "hello world".getBytes());

        mockMvc.perform(multipart("/api/v1/attachments").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value("att-1"))
                .andExpect(jsonPath("$.items[0].fileName").value("hello.txt"))
                .andExpect(jsonPath("$.items[0].url").value("https://cdn.example.com/messaging/attachments/att-1.txt"));
    }

    @Test
    void sendDirectMessage_unknownTarget_returns404ErrorBody() throws Exception {
        when(messageService.sendDirectMessage(eq("user-1"), any()))
                .thenThrow(new ResourceNotFoundException("Khong tim thay user profile: user-2"));

        mockMvc.perform(post("/api/v1/messages/direct")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targetUserId": "user-2",
                                  "content": "Xin chao"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Khong tim thay user profile: user-2"));
    }

    @Test
    void createConversation_invalidParticipants_returns400ErrorBody() throws Exception {
        doThrow(new IllegalArgumentException("Direct conversation phai co dung 2 nguoi tham gia"))
                .when(conversationService).createDirectConversation(eq("user-1"), any());

        mockMvc.perform(post("/api/v1/conversations")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participantIds": ["user-1"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Direct conversation phai co dung 2 nguoi tham gia"));
    }

    @Test
    void addReaction_returnsUpdatedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-1")
                .conversationId("conv-1")
                .reactions(List.of(new Reaction(2, 128077, true)))
                .build();

        when(messageService.addReaction("user-1", "msg-1", 128077)).thenReturn(response);

        mockMvc.perform(post("/api/v1/messages/msg-1/reactions")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emoji": 128077
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-1"))
                .andExpect(jsonPath("$.reactions[0].emoji").value(new String(Character.toChars(128077))))
                .andExpect(jsonPath("$.reactions[0].emojiCode").value(128077))
                .andExpect(jsonPath("$.reactions[0].count").value(2))
                .andExpect(jsonPath("$.reactions[0].me").value(true));
    }

    @Test
    void pinMessage_returnsUpdatedPinnedFlag() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-1")
                .conversationId("conv-1")
                .pinned(true)
                .build();

        when(messageService.pinMessage("user-1", "msg-1")).thenReturn(response);

        mockMvc.perform(put("/api/v1/messages/msg-1/pin")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-1"))
                .andExpect(jsonPath("$.pinned").value(true));
    }

    @Test
    void revokeMessage_returnsDeletedShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-1")
                .conversationId("conv-1")
                .content("Tin nhan da duoc thu hoi")
                .deleted(true)
                .build();

        when(messageService.revokeMessage("user-1", "msg-1")).thenReturn(response);

        mockMvc.perform(put("/api/v1/messages/msg-1/revoke")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-1"))
                .andExpect(jsonPath("$.content").value("Tin nhan da duoc thu hoi"))
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void forwardMessage_returnsForwardedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-forward")
                .conversationId("conv-2")
                .content("Xin chao")
                .type("DEFAULT")
                .build();

        when(messageService.forwardMessage(eq("user-1"), eq("msg-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/messages/msg-1/forward")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId": "conv-2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-forward"))
                .andExpect(jsonPath("$.conversationId").value("conv-2"))
                .andExpect(jsonPath("$.content").value("Xin chao"));
    }

    @Test
    void deleteMessageForMe_returnsAckShape() throws Exception {
        DeleteMessageResponse response = DeleteMessageResponse.builder()
                .messageId("msg-1")
                .conversationId("conv-1")
                .deletedForMe(true)
                .deletedForEveryone(false)
                .build();

        when(messageService.deleteMessageForMe("user-1", "msg-1")).thenReturn(response);

        mockMvc.perform(delete("/api/v1/messages/msg-1")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value("msg-1"))
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.deletedForMe").value(true));
    }

    @Test
    void deleteMessageForEveryone_returnsUpdatedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-1")
                .conversationId("conv-1")
                .content("Tin nhan da bi xoa")
                .deleted(true)
                .deletedForEveryone(true)
                .build();

        when(messageService.deleteMessageForEveryone("user-1", "msg-1")).thenReturn(response);

        mockMvc.perform(delete("/api/v1/messages/msg-1/everyone")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-1"))
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.content").value("Tin nhan da bi xoa"))
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.deletedForEveryone").value(true));
    }

    @Test
    void editMessage_returnsEditedShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-1")
                .conversationId("conv-1")
                .content("Moi")
                .editedTimeStamp(Instant.parse("2026-05-30T10:00:00Z"))
                .build();

        when(messageService.editMessage(eq("user-1"), eq("msg-1"), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/messages/msg-1")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Moi"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-1"))
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.content").value("Moi"))
                .andExpect(jsonPath("$.editedTimeStamp").value(1780135200.000000000D));
    }

    @Test
    void createPoll_returnsMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-poll")
                .conversationId("group-1")
                .type("DEFAULT")
                .poll(new Poll(
                        "An gi toi nay?",
                        List.of(new PollAnswer(1, "Pho"), new PollAnswer(2, "Bun bo")),
                        null,
                        false,
                        new PollResults(false, List.of(new PollAnswerCount(1, 0), new PollAnswerCount(2, 0)), List.of())
                ))
                .build();

        when(pollService.createPoll(eq("group-1"), eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/group-1/polls")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "An gi toi nay?",
                                  "options": ["Pho", "Bun bo"],
                                  "allowMultiSelect": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-poll"))
                .andExpect(jsonPath("$.conversationId").value("group-1"))
                .andExpect(jsonPath("$.poll.question").value("An gi toi nay?"))
                .andExpect(jsonPath("$.poll.answers[0].id").value(1));
    }

    @Test
    void votePoll_returnsUpdatedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-poll")
                .conversationId("group-1")
                .poll(new Poll(
                        "An gi toi nay?",
                        List.of(new PollAnswer(1, "Pho"), new PollAnswer(2, "Bun bo")),
                        null,
                        false,
                        new PollResults(false, List.of(new PollAnswerCount(1, 1), new PollAnswerCount(2, 0)), List.of(1))
                ))
                .build();

        when(pollService.vote(eq("group-1"), eq("msg-poll"), eq("user-2"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/group-1/polls/msg-poll/votes")
                        .header("X-User-Id", "user-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "answerIds": [1]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-poll"))
                .andExpect(jsonPath("$.poll.results.answerCounts[0].count").value(1))
                .andExpect(jsonPath("$.poll.results.selectedAnswerIds[0]").value(1));
    }

    @Test
    void addPollOption_returnsUpdatedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-poll")
                .conversationId("group-1")
                .poll(new Poll(
                        "An gi toi nay?",
                        List.of(new PollAnswer(1, "Pho"), new PollAnswer(2, "Bun bo"), new PollAnswer(3, "Com tam")),
                        null,
                        false,
                        new PollResults(false, List.of(new PollAnswerCount(1, 0), new PollAnswerCount(2, 0), new PollAnswerCount(3, 0)), List.of())
                ))
                .build();

        when(pollService.addOption(eq("group-1"), eq("msg-poll"), eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/group-1/polls/msg-poll/options")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Com tam"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poll.answers[2].id").value(3))
                .andExpect(jsonPath("$.poll.answers[2].content").value("Com tam"));
    }

    @Test
    void closePoll_returnsUpdatedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-poll")
                .conversationId("group-1")
                .poll(new Poll(
                        "An gi toi nay?",
                        List.of(new PollAnswer(1, "Pho"), new PollAnswer(2, "Bun bo")),
                        null,
                        false,
                        new PollResults(true, List.of(new PollAnswerCount(1, 2), new PollAnswerCount(2, 1)), List.of())
                ))
                .build();

        when(pollService.closePoll("group-1", "msg-poll", "user-1")).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/group-1/polls/msg-poll/close")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poll.results.finalized").value(true));
    }

    @Test
    void getPollAnswerVotes_returnsVoterListShape() throws Exception {
        PollAnswerVotesResponse response = PollAnswerVotesResponse.builder()
                .conversationId("group-1")
                .messageId("msg-poll")
                .answerId(1)
                .total(1)
                .voters(List.of(new User("user-2", "User 2", "https://example/avatar.png")))
                .build();

        when(pollService.getAnswerVotes("group-1", "msg-poll", 1, "user-1")).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/conversations/group-1/polls/msg-poll/answers/1")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerId").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.voters[0].userId").value("user-2"));
    }

    @Test
    void createCall_returnsMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-call")
                .conversationId("conv-1")
                .call(new CallInfo(
                        "VIDEO",
                        "ONGOING",
                        List.of(new User("user-1", "User 1", null), new User("user-2", "User 2", null)),
                        Instant.parse("2026-05-30T10:00:00Z"),
                        null
                ))
                .build();

        when(callService.createCall(eq("conv-1"), eq("user-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/conv-1/calls")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "VIDEO",
                                  "userIds": ["user-1", "user-2"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-call"))
                .andExpect(jsonPath("$.call.type").value("VIDEO"))
                .andExpect(jsonPath("$.call.status").value("ONGOING"));
    }

    @Test
    void endCall_returnsUpdatedMessageShape() throws Exception {
        MessageResponse response = MessageResponse.builder()
                .id("msg-call")
                .conversationId("conv-1")
                .call(new CallInfo(
                        "VIDEO",
                        "ENDED",
                        List.of(new User("user-1", "User 1", null), new User("user-2", "User 2", null)),
                        Instant.parse("2026-05-30T10:00:00Z"),
                        Instant.parse("2026-05-30T10:30:00Z")
                ))
                .build();

        when(callService.endCall("conv-1", "msg-call", "user-1")).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/conv-1/calls/msg-call/end")
                        .header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("msg-call"))
                .andExpect(jsonPath("$.call.status").value("ENDED"));
    }
}

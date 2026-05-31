package com.fit.se.poll.service;

import com.fit.se.poll.dto.AddPollOptionRequest;
import com.fit.se.poll.dto.CreatePollRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.poll.dto.PollAnswerVotesResponse;
import com.fit.se.poll.dto.VotePollRequest;
import com.fit.se.user.domain.User;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Member;
import com.fit.se.message.domain.Message;
import com.fit.se.poll.domain.Poll;
import com.fit.se.poll.domain.PollAnswer;
import com.fit.se.poll.domain.PollAnswerCount;
import com.fit.se.poll.domain.PollResults;
import com.fit.se.poll.domain.PollVote;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.message.service.MessageService;
import com.fit.se.poll.repository.PollVoteRepository;
import com.fit.se.user.service.UserDirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PollVoteRepository pollVoteRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private MessageService messageService;

    @Mock
    private UserDirectoryService userDirectoryService;

    private PollService pollService;

    @BeforeEach
    void setUp() {
        pollService = new PollService(
                messageRepository,
                pollVoteRepository,
                conversationService,
                messageService,
                userDirectoryService
        );
    }

    @Test
    void createPoll_buildsPollMessageViaMessageService() {
        CreatePollRequest request = new CreatePollRequest();
        request.setQuestion("An gi toi nay?");
        request.setOptions(List.of("Pho", "Bun bo"));

        MessageResponse expected = MessageResponse.builder()
                .id("msg-poll")
                .conversationId("group-1")
                .build();
        when(messageService.sendMessage(eq("user-1"), any())).thenReturn(expected);

        MessageResponse response = pollService.createPoll("group-1", "user-1", request);

        assertThat(response.getId()).isEqualTo("msg-poll");
        verify(messageService).sendMessage(eq("user-1"), any());
    }

    @Test
    void vote_replacesExistingVotesAndRefreshesResults() {
        Message message = pollMessage();
        when(messageRepository.findById("msg-poll")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("group-1", "user-2")).thenReturn(new Member());
        when(pollVoteRepository.findByMessageIdAndUserId("msg-poll", "user-2"))
                .thenReturn(List.of(PollVote.builder().id("vote-old").messageId("msg-poll").conversationId("group-1").userId("user-2").answerId(2).build()));
        when(pollVoteRepository.findByMessageId("msg-poll"))
                .thenReturn(List.of(PollVote.builder().messageId("msg-poll").userId("user-2").answerId(1).build()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.toMessageResponse(any(Message.class), eq("user-2")))
                .thenReturn(MessageResponse.builder().id("msg-poll").conversationId("group-1").build());

        VotePollRequest request = new VotePollRequest();
        request.setAnswerIds(List.of(1));

        MessageResponse response = pollService.vote("group-1", "msg-poll", "user-2", request);

        assertThat(response.getId()).isEqualTo("msg-poll");
        assertThat(message.getPoll().getResults().getAnswerCounts().getFirst().getCount()).isEqualTo(1);
        verify(pollVoteRepository, times(1)).delete(any(PollVote.class));
        verify(pollVoteRepository, times(1)).save(any(PollVote.class));
        verify(conversationService).publishMessageUpdateEvent(message);
    }

    @Test
    void addOption_appendsAnswerToPoll() {
        Message message = pollMessage();
        when(messageRepository.findById("msg-poll")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("group-1", "user-1")).thenReturn(new Member());
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversationService.toMessageResponse(any(Message.class), eq("user-1")))
                .thenReturn(MessageResponse.builder().id("msg-poll").conversationId("group-1").build());

        AddPollOptionRequest request = new AddPollOptionRequest();
        request.setContent("Com tam");

        pollService.addOption("group-1", "msg-poll", "user-1", request);

        assertThat(message.getPoll().getAnswers()).hasSize(3);
        assertThat(message.getPoll().getAnswers().getLast().getContent()).isEqualTo("Com tam");
        verify(conversationService).publishMessageUpdateEvent(message);
    }

    @Test
    void getAnswerVotes_returnsResolvedUserProfiles() {
        Message message = pollMessage();
        UserProfileDocument profile = new UserProfileDocument();
        profile.setId("user-2");
        profile.setDisplayName("User 2");
        profile.setAvatarUrl("https://example/avatar.png");

        when(messageRepository.findById("msg-poll")).thenReturn(Optional.of(message));
        when(conversationService.getActiveMembership("group-1", "user-1")).thenReturn(new Member());
        when(pollVoteRepository.findByMessageId("msg-poll"))
                .thenReturn(List.of(PollVote.builder().messageId("msg-poll").userId("user-2").answerId(1).build()));
        when(userDirectoryService.getRequired("user-2")).thenReturn(profile);

        PollAnswerVotesResponse response = pollService.getAnswerVotes("group-1", "msg-poll", 1, "user-1");

        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getVoters()).hasSize(1);
        assertThat(response.getVoters().getFirst().getUserId()).isEqualTo("user-2");
        assertThat(response.getVoters().getFirst().getNick()).isEqualTo("User 2");
    }

    private Message pollMessage() {
        Message message = new Message();
        message.setId("msg-poll");
        message.setConversationId("group-1");
        message.setUser(new User("user-1", "Owner", null));
        message.setPoll(new Poll(
                "An gi toi nay?",
                List.of(new PollAnswer(1, "Pho"), new PollAnswer(2, "Bun bo")),
                null,
                false,
                new PollResults(false, List.of(new PollAnswerCount(1, 0), new PollAnswerCount(2, 0)), List.of())
        ));
        return message;
    }
}

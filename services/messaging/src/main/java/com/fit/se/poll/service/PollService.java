package com.fit.se.poll.service;

import com.fit.se.poll.dto.AddPollOptionRequest;
import com.fit.se.poll.dto.CreatePollRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.poll.dto.PollAnswerVotesResponse;
import com.fit.se.poll.dto.VotePollRequest;
import com.fit.se.user.domain.User;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.message.domain.Message;
import com.fit.se.poll.domain.Poll;
import com.fit.se.poll.domain.PollAnswer;
import com.fit.se.poll.domain.PollAnswerCount;
import com.fit.se.poll.domain.PollResults;
import com.fit.se.poll.domain.PollVote;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.message.dto.SendMessageRequest;
import com.fit.se.message.service.MessageService;
import com.fit.se.poll.repository.PollVoteRepository;
import com.fit.se.user.service.UserDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PollService {
    private final MessageRepository messageRepository;
    private final PollVoteRepository pollVoteRepository;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserDirectoryService userDirectoryService;

    public MessageResponse createPoll(String conversationId, String userId, CreatePollRequest request) {
        validateCreateRequest(request);

        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setConversationId(conversationId);
        sendMessageRequest.setContent(request.getContent());

        Poll poll = new Poll();
        poll.setQuestion(request.getQuestion().trim());
        poll.setAnswers(buildAnswers(request.getOptions()));
        poll.setExpiry(request.getExpiry());
        poll.setAllowMultiSelect(Boolean.TRUE.equals(request.getAllowMultiSelect()));
        poll.setResults(new PollResults(false, buildAnswerCounts(buildAnswers(request.getOptions())), List.of()));
        sendMessageRequest.setPoll(poll);
        return messageService.sendMessage(userId, sendMessageRequest);
    }

    public MessageResponse vote(String conversationId, String messageId, String userId, VotePollRequest request) {
        Message message = getPollMessage(conversationId, messageId, userId);
        Poll poll = requireOpenPoll(message);

        List<Integer> answerIds = normalizeAnswerIds(request == null ? null : request.getAnswerIds(), poll);
        if (!poll.isAllowMultiSelect() && answerIds.size() > 1) {
            throw new IllegalArgumentException("Poll nay khong ho tro chon nhieu dap an");
        }

        List<PollVote> existingVotes = pollVoteRepository.findByMessageIdAndUserId(messageId, userId);
        Set<Integer> nextAnswerIds = new HashSet<>(answerIds);
        for (PollVote vote : existingVotes) {
            if (!nextAnswerIds.contains(vote.getAnswerId())) {
                pollVoteRepository.delete(vote);
            }
        }

        Set<Integer> existingAnswerIds = existingVotes.stream()
                .map(PollVote::getAnswerId)
                .collect(java.util.stream.Collectors.toSet());
        Instant now = Instant.now();
        for (Integer answerId : nextAnswerIds) {
            if (existingAnswerIds.contains(answerId)) {
                continue;
            }
            pollVoteRepository.save(PollVote.builder()
                    .messageId(messageId)
                    .conversationId(conversationId)
                    .userId(userId)
                    .answerId(answerId)
                    .createdAt(now)
                    .build());
        }

        Message saved = refreshPollResults(message);
        conversationService.publishMessageUpdateEvent(saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse addOption(String conversationId, String messageId, String userId, AddPollOptionRequest request) {
        Message message = getPollMessage(conversationId, messageId, userId);
        requirePollOwner(message, userId);
        Poll poll = requireOpenPoll(message);

        String optionContent = request == null || request.getContent() == null ? "" : request.getContent().trim();
        if (optionContent.isBlank()) {
            throw new IllegalArgumentException("Noi dung lua chon khong duoc de trong");
        }

        List<PollAnswer> answers = poll.getAnswers() == null ? new ArrayList<>() : new ArrayList<>(poll.getAnswers());
        int nextId = answers.stream().map(PollAnswer::getId).max(Comparator.naturalOrder()).orElse(0) + 1;
        answers.add(new PollAnswer(nextId, optionContent));
        poll.setAnswers(answers);

        PollResults results = poll.getResults();
        if (results == null) {
            results = new PollResults();
            poll.setResults(results);
        }
        List<PollAnswerCount> counts = results.getAnswerCounts() == null ? new ArrayList<>() : new ArrayList<>(results.getAnswerCounts());
        counts.add(new PollAnswerCount(nextId, 0));
        results.setAnswerCounts(counts);

        Message saved = messageRepository.save(message);
        conversationService.publishMessageUpdateEvent(saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse closePoll(String conversationId, String messageId, String userId) {
        Message message = getPollMessage(conversationId, messageId, userId);
        requirePollOwner(message, userId);
        Poll poll = requirePoll(message);

        PollResults results = poll.getResults();
        if (results == null) {
            results = new PollResults();
            poll.setResults(results);
        }
        results.setFinalized(true);

        Message saved = refreshPollResults(message);
        conversationService.publishMessageUpdateEvent(saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse deletePoll(String conversationId, String messageId, String userId) {
        Message message = getPollMessage(conversationId, messageId, userId);
        requirePollOwner(message, userId);
        pollVoteRepository.deleteByMessageId(messageId);
        return messageService.revokeMessage(userId, messageId);
    }

    public PollAnswerVotesResponse getAnswerVotes(String conversationId, String messageId, int answerId, String userId) {
        Message message = getPollMessage(conversationId, messageId, userId);
        Poll poll = requirePoll(message);
        boolean answerExists = poll.getAnswers() != null && poll.getAnswers().stream().anyMatch(item -> item.getId() == answerId);
        if (!answerExists) {
            throw new IllegalArgumentException("Khong tim thay dap an cua poll");
        }

        List<User> voters = pollVoteRepository.findByMessageId(messageId).stream()
                .filter(item -> item.getAnswerId() == answerId)
                .map(item -> {
                    UserProfileDocument profile = userDirectoryService.getRequired(item.getUserId());
                    return new User(profile.getId(), profile.getDisplayName(), profile.getAvatarUrl());
                })
                .toList();

        return PollAnswerVotesResponse.builder()
                .conversationId(conversationId)
                .messageId(messageId)
                .answerId(answerId)
                .total(voters.size())
                .voters(voters)
                .build();
    }

    private void validateCreateRequest(CreatePollRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Du lieu gui len khong hop le");
        }
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new IllegalArgumentException("Cau hoi poll khong duoc de trong");
        }
        List<String> options = request.getOptions() == null ? List.of() : request.getOptions().stream()
                .filter(item -> item != null && !item.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (options.size() < 2) {
            throw new IllegalArgumentException("Poll phai co it nhat 2 lua chon");
        }
    }

    private List<PollAnswer> buildAnswers(List<String> rawOptions) {
        List<String> options = rawOptions == null ? List.of() : rawOptions.stream()
                .filter(item -> item != null && !item.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        List<PollAnswer> answers = new ArrayList<>();
        for (int index = 0; index < options.size(); index++) {
            answers.add(new PollAnswer(index + 1, options.get(index)));
        }
        return answers;
    }

    private List<PollAnswerCount> buildAnswerCounts(List<PollAnswer> answers) {
        return answers.stream()
                .map(item -> new PollAnswerCount(item.getId(), 0))
                .toList();
    }

    private Message getPollMessage(String conversationId, String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay message"));
        if (!conversationId.equals(message.getConversationId())) {
            throw new ResourceNotFoundException("Khong tim thay poll trong conversation");
        }
        conversationService.getActiveMembership(conversationId, userId);
        return message;
    }

    private Poll requirePoll(Message message) {
        if (message.getPoll() == null) {
            throw new IllegalArgumentException("Message nay khong phai poll");
        }
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Poll da bi thu hoi");
        }
        return message.getPoll();
    }

    private Poll requireOpenPoll(Message message) {
        Poll poll = requirePoll(message);
        if (poll.getResults() != null && poll.getResults().isFinalized()) {
            throw new IllegalArgumentException("Poll da dong");
        }
        if (poll.getExpiry() != null && poll.getExpiry().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Poll da het han");
        }
        return poll;
    }

    private void requirePollOwner(Message message, String userId) {
        if (message.getUser() == null || !userId.equals(message.getUser().getUserId())) {
            throw new IllegalArgumentException("Chi nguoi tao poll moi duoc thao tac");
        }
    }

    private List<Integer> normalizeAnswerIds(List<Integer> rawAnswerIds, Poll poll) {
        if (rawAnswerIds == null || rawAnswerIds.isEmpty()) {
            throw new IllegalArgumentException("Can it nhat mot dap an de vote");
        }
        Set<Integer> validIds = poll.getAnswers().stream()
                .map(PollAnswer::getId)
                .collect(java.util.stream.Collectors.toSet());
        List<Integer> normalized = rawAnswerIds.stream()
                .filter(item -> item != null)
                .distinct()
                .toList();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Can it nhat mot dap an de vote");
        }
        if (!validIds.containsAll(normalized)) {
            throw new IllegalArgumentException("Co dap an khong hop le");
        }
        return normalized;
    }

    private Message refreshPollResults(Message message) {
        Poll poll = requirePoll(message);
        List<PollVote> votes = pollVoteRepository.findByMessageId(message.getId());
        List<PollAnswerCount> counts = poll.getAnswers().stream()
                .map(answer -> new PollAnswerCount(answer.getId(), (int) votes.stream().filter(item -> item.getAnswerId() == answer.getId()).count()))
                .toList();
        PollResults results = poll.getResults() == null ? new PollResults() : poll.getResults();
        results.setAnswerCounts(counts);
        if (results.getSelectedAnswerIds() == null) {
            results.setSelectedAnswerIds(List.of());
        }
        poll.setResults(results);
        return messageRepository.save(message);
    }
}

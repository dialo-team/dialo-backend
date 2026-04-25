package com.fit.se.service;

import com.fit.se.dto.AddPollOptionRequest;
import com.fit.se.dto.CreatePollRequest;
import com.fit.se.dto.DeleteMessageRequest;
import com.fit.se.dto.EditMessageRequest;
import com.fit.se.dto.ForwardMessageRequest;
import com.fit.se.dto.MessageReactionRequest;
import com.fit.se.dto.MessageResponse;
import com.fit.se.dto.SendMessageRequest;
import com.fit.se.dto.VotePollRequest;
import com.fit.se.entity.AttachmentMetadata;
import com.fit.se.entity.Conversation;
import com.fit.se.entity.Message;
import com.fit.se.entity.MessageReaction;
import com.fit.se.entity.PollOption;
import com.fit.se.entity.PollPayload;
import com.fit.se.repository.ConversationRepository;
import com.fit.se.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final UserProfileService userProfileService;
    private final FileStorageService fileStorageService;

    public MessageResponse sendMessage(SendMessageRequest request) {
        Conversation conversation = conversationService.getConversationEntity(request.getConversationId());
        conversationService.validateParticipant(conversation, request.getSenderId());
        userProfileService.findEntity(request.getSenderId());

        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(request.getSenderId())
                .receiverId(resolveReceiverId(conversation, request.getSenderId()))
                .type(normalizeMessageType(request.getType()))
                .content(request.getContent())
                .durationSeconds(request.getDurationSeconds())
                .location(request.getLocation())
                .contactCard(request.getContactCard())
                .deletedFor(new HashSet<>())
                .readBy(buildInitialReadBy(request.getSenderId()))
                .reactions(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return persistAndPublish(message, conversation, request.getSenderId(), true);
    }

    public MessageResponse createPoll(String senderId, CreatePollRequest request) {
        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new IllegalArgumentException("Poll must contain at least 2 options");
        }

        Conversation conversation = conversationService.getConversationEntity(request.getConversationId());
        conversationService.validateParticipant(conversation, senderId);
        userProfileService.findEntity(senderId);

        List<PollOption> options = request.getOptions().stream()
                .map(this::requireText)
                .distinct()
                .map(content -> PollOption.builder()
                        .id(UUID.randomUUID().toString())
                        .content(content)
                        .voterIds(new ArrayList<>())
                        .build())
                .toList();

        if (options.size() < 2) {
            throw new IllegalArgumentException("Poll must contain at least 2 distinct options");
        }

        String title = requireText(request.getTitle());
        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(senderId)
                .receiverId(resolveReceiverId(conversation, senderId))
                .type("POLL")
                .content(title)
                .poll(PollPayload.builder()
                        .title(title)
                        .options(new ArrayList<>(options))
                        .closed(false)
                        .build())
                .deletedFor(new HashSet<>())
                .readBy(buildInitialReadBy(senderId))
                .reactions(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return persistAndPublish(message, conversation, senderId, true);
    }

    public MessageResponse sendFile(String conversationId, String senderId, MultipartFile file) {
        Conversation conversation = conversationService.getConversationEntity(conversationId);
        conversationService.validateParticipant(conversation, senderId);
        userProfileService.findEntity(senderId);

        FileStorageService.StoredFile storedFile = fileStorageService.store(file);
        String type = resolveMessageType(storedFile.getMimeType());
        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .receiverId(resolveReceiverId(conversation, senderId))
                .type(type)
                .content(storedFile.getOriginalFileName())
                .attachment(AttachmentMetadata.builder()
                        .fileName(storedFile.getOriginalFileName())
                        .fileUrl(storedFile.getPublicUrl())
                        .mimeType(storedFile.getMimeType())
                        .size(storedFile.getSize())
                        .thumbnailUrl("IMAGE".equals(type) ? storedFile.getPublicUrl() : null)
                        .build())
                .deletedFor(new HashSet<>())
                .readBy(buildInitialReadBy(senderId))
                .reactions(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return persistAndPublish(message, conversation, senderId, true);
    }

    public MessageResponse forwardMessage(ForwardMessageRequest request) {
        Message source = messageRepository.findById(request.getSourceMessageId()).orElseThrow(() -> new IllegalArgumentException("Source message not found"));
        Conversation conversation = conversationService.getConversationEntity(request.getTargetConversationId());
        conversationService.validateParticipant(conversation, request.getSenderId());

        Message message = Message.builder()
                .conversationId(request.getTargetConversationId())
                .senderId(request.getSenderId())
                .receiverId(resolveReceiverId(conversation, request.getSenderId()))
                .type(source.getType())
                .content(source.isRevoked() ? "Message was revoked" : source.getContent())
                .attachment(source.getAttachment())
                .durationSeconds(source.getDurationSeconds())
                .location(source.getLocation())
                .contactCard(source.getContactCard())
                .forwardedFromMessageId(source.getId())
                .deletedFor(new HashSet<>())
                .readBy(buildInitialReadBy(request.getSenderId()))
                .reactions(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return persistAndPublish(message, conversation, request.getSenderId(), true);
    }

    public MessageResponse editMessage(String messageId, String requesterId, EditMessageRequest request) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        if (message.isSystem()) throw new IllegalArgumentException("System message cannot be edited");
        if ("POLL".equalsIgnoreCase(message.getType())) throw new IllegalArgumentException("Poll message cannot be edited");
        if (!requesterId.equals(message.getSenderId())) throw new IllegalArgumentException("Only sender can edit the message");
        if (message.isRevoked()) throw new IllegalArgumentException("Revoked message cannot be edited");

        message.setContent(request.getContent());
        message.setDurationSeconds(request.getDurationSeconds());
        message.setLocation(request.getLocation());
        message.setContactCard(request.getContactCard());
        message.setEdited(true);
        message.setEditedAt(Instant.now());
        message.setUpdatedAt(Instant.now());
        Message saved = messageRepository.save(message);
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        if (conversation.getLastMessage() != null && messageId.equals(conversation.getLastMessage().getMessageId())) {
            conversationService.updateConversationLastMessage(conversation, saved, false);
            conversation = conversationRepository.findById(conversation.getId()).orElse(conversation);
        }
        conversationService.pushConversationEvent(saved, conversation);
        conversationService.pushInboxUpdates(conversation);
        return conversationService.toMessageResponse(saved, requesterId, conversation);
    }

    public MessageResponse reactToMessage(String messageId, String requesterId, MessageReactionRequest request) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        conversationService.validateParticipant(conversation, requesterId);
        ensureCollections(message);
        message.getReactions().removeIf(reaction -> requesterId.equals(reaction.getUserId()));
        message.getReactions().add(MessageReaction.builder().userId(requesterId).emoji(request.getEmoji()).reactedAt(Instant.now()).build());
        message.setUpdatedAt(Instant.now());
        Message saved = messageRepository.save(message);
        conversationService.pushConversationEvent(saved, conversation);
        return conversationService.toMessageResponse(saved, requesterId, conversation);
    }

    public MessageResponse votePoll(String messageId, String requesterId, VotePollRequest request) {
        Message message = getPollMessage(messageId);
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        conversationService.validateParticipant(conversation, requesterId);
        ensurePoll(message);
        if (message.getPoll().isClosed()) {
            throw new IllegalArgumentException("Poll is already closed");
        }

        HashSet<String> selectedOptionIds = new HashSet<>(request == null || request.getOptionIds() == null ? List.of() : request.getOptionIds());
        for (String optionId : selectedOptionIds) {
            findPollOption(message, optionId);
        }

        for (PollOption option : message.getPoll().getOptions()) {
            option.getVoterIds().removeIf(requesterId::equals);
            if (selectedOptionIds.contains(option.getId())) {
                option.getVoterIds().add(requesterId);
            }
        }

        message.setUpdatedAt(Instant.now());
        Message saved = messageRepository.save(message);
        conversationService.pushConversationEvent(saved, conversation);
        return conversationService.toMessageResponse(saved, requesterId, conversation);
    }

    public MessageResponse addPollOption(String messageId, String requesterId, AddPollOptionRequest request) {
        Message message = getPollMessage(messageId);
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        conversationService.validateParticipant(conversation, requesterId);
        ensurePoll(message);
        if (message.getPoll().isClosed()) {
            throw new IllegalArgumentException("Poll is already closed");
        }

        message.getPoll().getOptions().add(PollOption.builder()
                .id(UUID.randomUUID().toString())
                .content(requireText(request.getContent()))
                .voterIds(new ArrayList<>())
                .build());
        message.setUpdatedAt(Instant.now());
        Message saved = messageRepository.save(message);
        conversationService.pushConversationEvent(saved, conversation);
        return conversationService.toMessageResponse(saved, requesterId, conversation);
    }

    public MessageResponse closePoll(String messageId, String requesterId) {
        Message message = getPollMessage(messageId);
        if (!requesterId.equals(message.getSenderId())) {
            throw new IllegalArgumentException("Only sender can close the poll");
        }
        ensurePoll(message);
        if (!message.getPoll().isClosed()) {
            message.getPoll().setClosed(true);
            message.getPoll().setClosedAt(Instant.now());
            message.getPoll().setClosedByUserId(requesterId);
            message.setUpdatedAt(Instant.now());
        }
        Message saved = messageRepository.save(message);
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        conversationService.pushConversationEvent(saved, conversation);
        return conversationService.toMessageResponse(saved, requesterId, conversation);
    }

    public MessageResponse revokeMessage(String messageId, String requesterId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        if (message.isSystem()) throw new IllegalArgumentException("System message cannot be revoked");
        if (!requesterId.equals(message.getSenderId())) throw new IllegalArgumentException("Only sender can revoke the message");
        message.setRevoked(true);
        message.setAttachment(null);
        message.setLocation(null);
        message.setContactCard(null);
        message.setPoll(null);
        message.setContent("Message was revoked");
        message.setUpdatedAt(Instant.now());
        Message saved = messageRepository.save(message);
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        if (conversation.getLastMessage() != null && messageId.equals(conversation.getLastMessage().getMessageId())) {
            conversationService.updateConversationLastMessage(conversation, saved, false);
            conversation = conversationRepository.findById(conversation.getId()).orElse(conversation);
        }
        conversationService.clearPinnedMessageIfMatches(conversation.getId(), messageId);
        conversationService.pushConversationEvent(saved, conversation);
        conversationService.pushInboxUpdates(conversation);
        return conversationService.toMessageResponse(saved, requesterId, conversation);
    }

    public MessageResponse deleteForMe(String messageId, DeleteMessageRequest request) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        Conversation conversation = conversationService.getConversationEntity(message.getConversationId());
        conversationService.validateParticipant(conversation, request.getUserId());
        ensureCollections(message);
        message.getDeletedFor().add(request.getUserId());
        message.setUpdatedAt(Instant.now());
        Message saved = messageRepository.save(message);
        conversationService.pushConversationEvent(saved, conversation);
        return conversationService.toMessageResponse(saved, request.getUserId(), conversation);
    }

    private MessageResponse persistAndPublish(Message message, Conversation conversation, String requesterId, boolean incrementUnread) {
        Message saved = messageRepository.save(message);
        conversationService.updateConversationLastMessage(conversation, saved, incrementUnread);
        Conversation refreshed = conversationRepository.findById(conversation.getId()).orElse(conversation);
        conversationService.pushConversationEvent(saved, refreshed);
        conversationService.pushInboxUpdates(refreshed);
        return conversationService.toMessageResponse(saved, requesterId, refreshed);
    }

    private String resolveReceiverId(Conversation conversation, String senderId) {
        if (!"direct".equalsIgnoreCase(conversation.getType())) {
            return null;
        }
        return conversation.getParticipants().stream().filter(id -> !id.equals(senderId)).findFirst().orElse(null);
    }

    private HashMap<String, Instant> buildInitialReadBy(String senderId) {
        HashMap<String, Instant> readBy = new HashMap<>();
        readBy.put(senderId, Instant.now());
        return readBy;
    }

    private String normalizeMessageType(String type) {
        String normalized = type == null ? "TEXT" : type.trim().toUpperCase();
        return normalized.isBlank() ? "TEXT" : normalized;
    }

    private String resolveMessageType(String mimeType) {
        if (mimeType.startsWith("audio/")) return "VOICE";
        if (mimeType.startsWith("image/gif")) return "GIF";
        if (mimeType.startsWith("image/")) return "IMAGE";
        if (mimeType.startsWith("video/")) return "VIDEO";
        return "FILE";
    }

    private void ensureCollections(Message message) {
        if (message.getDeletedFor() == null) message.setDeletedFor(new HashSet<>());
        if (message.getReadBy() == null) message.setReadBy(new HashMap<>());
        if (message.getReactions() == null) message.setReactions(new ArrayList<>());
    }

    private Message getPollMessage(String messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        if (!"POLL".equalsIgnoreCase(message.getType())) {
            throw new IllegalArgumentException("Message is not a poll");
        }
        return message;
    }

    private void ensurePoll(Message message) {
        if (message.getPoll() == null) {
            throw new IllegalArgumentException("Poll payload not found");
        }
        if (message.getPoll().getOptions() == null) {
            message.getPoll().setOptions(new ArrayList<>());
        }
        for (PollOption option : message.getPoll().getOptions()) {
            if (option.getVoterIds() == null) {
                option.setVoterIds(new ArrayList<>());
            }
        }
    }

    private PollOption findPollOption(Message message, String optionId) {
        return message.getPoll().getOptions().stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Poll option not found"));
    }

    private String requireText(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Value must not be blank");
        }
        return normalized;
    }
}

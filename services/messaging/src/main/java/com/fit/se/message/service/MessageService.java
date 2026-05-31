package com.fit.se.message.service;

import com.fit.se.message.dto.DeleteMessageResponse;
import com.fit.se.message.dto.EditMessageRequest;
import com.fit.se.message.dto.MessageResponse;
import com.fit.se.message.dto.ForwardMessageRequest;
import com.fit.se.message.dto.SendDirectMessageRequest;
import com.fit.se.message.dto.SendMessageRequest;
import com.fit.se.user.domain.User;
import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.conversation.domain.Conversation;
import com.fit.se.message.domain.MessageHidden;
import com.fit.se.attachment.domain.Attachment;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.message.domain.Message;
import com.fit.se.message.domain.MessageReference;
import com.fit.se.message.domain.MessageSnapshot;
import com.fit.se.message.domain.MessageType;
import com.fit.se.message.domain.MessageReaction;
import com.fit.se.message.domain.Sticker;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.conversation.service.ConversationService;
import com.fit.se.message.repository.MessageHiddenRepository;
import com.fit.se.message.repository.MessageReactionRepository;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.poll.domain.Poll;
import com.fit.se.user.service.UserDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageHiddenRepository messageHiddenRepository;
    private final MessageReactionRepository messageReactionRepository;
    private final ConversationService conversationService;
    private final UserDirectoryService userDirectoryService;

    public MessageResponse sendMessage(String senderId, SendMessageRequest request) {
        Conversation conversation = conversationService.getConversationEntity(request.getConversationId());
        conversationService.getActiveMembership(conversation.getId(), senderId);
        Message saved = saveMessage(
                conversation,
                senderId,
                request.getContent(),
                request.getAttachments(),
                request.getStickers(),
                normalizeMentions(conversation.getId(), request.getMentions()),
                request.getMentionEveryone(),
                normalizeReference(conversation.getId(), request.getReference()),
                request.getPoll(),
                request.getCall()
        );
        return conversationService.toMessageResponse(saved, senderId);
    }

    public MessageResponse sendDirectMessage(String senderId, SendDirectMessageRequest request) {
        if (senderId.equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Khong the tu nhan tin cho chinh minh");
        }
        userDirectoryService.getRequired(senderId);
        userDirectoryService.getRequired(request.getTargetUserId());
        Conversation conversation = conversationService.ensureDirectConversation(senderId, request.getTargetUserId());
        conversationService.getActiveMembership(conversation.getId(), senderId);
        Message saved = saveMessage(
                conversation,
                senderId,
                request.getContent(),
                request.getAttachments(),
                request.getStickers(),
                normalizeMentions(conversation.getId(), request.getMentions()),
                request.getMentionEveryone(),
                normalizeReference(conversation.getId(), request.getReference()),
                request.getPoll(),
                request.getCall()
        );
        return conversationService.toMessageResponse(saved, senderId);
    }

    public MessageResponse addReaction(String userId, String messageId, int emoji) {
        Message message = getAccessibleMessage(messageId, userId);
        if (emoji <= 0) {
            throw new IllegalArgumentException("Emoji khong hop le");
        }
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the thao tac tren tin nhan da thu hoi");
        }

        if (messageReactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji).isEmpty()) {
            MessageReaction reaction = new MessageReaction();
            reaction.setMessageId(messageId);
            reaction.setConversationId(message.getConversationId());
            reaction.setUserId(userId);
            reaction.setEmoji(emoji);
            reaction.setQuantity(1);
            messageReactionRepository.save(reaction);
        }

        Message refreshed = refreshMessageState(message);
        conversationService.publishMessageUpdateEvent(refreshed);
        return conversationService.toMessageResponse(refreshed, userId);
    }

    public MessageResponse removeReaction(String userId, String messageId, int emoji) {
        Message message = getAccessibleMessage(messageId, userId);
        if (emoji <= 0) {
            throw new IllegalArgumentException("Emoji khong hop le");
        }
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the thao tac tren tin nhan da thu hoi");
        }

        messageReactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji)
                .ifPresent(messageReactionRepository::delete);

        Message refreshed = refreshMessageState(message);
        conversationService.publishMessageUpdateEvent(refreshed);
        return conversationService.toMessageResponse(refreshed, userId);
    }

    public MessageResponse pinMessage(String userId, String messageId) {
        Message message = getAccessibleMessage(messageId, userId);
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the thao tac tren tin nhan da thu hoi");
        }
        if (!Boolean.TRUE.equals(message.getPinned())) {
            long pinnedCount = messageRepository.findByConversationIdOrderByPositionAsc(message.getConversationId()).stream()
                    .filter(item -> item.getDeletedTimeStamp() == null)
                    .filter(item -> Boolean.TRUE.equals(item.getPinned()))
                    .count();
            if (pinnedCount >= 3) {
                throw new IllegalArgumentException("Moi cuoc tro chuyen chi duoc ghim toi da 3 tin nhan");
            }
        }
        message.setPinned(true);
        Message saved = messageRepository.save(message);
        conversationService.publishMessageUpdateEvent(saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse unpinMessage(String userId, String messageId) {
        Message message = getAccessibleMessage(messageId, userId);
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the thao tac tren tin nhan da thu hoi");
        }
        message.setPinned(false);
        Message saved = messageRepository.save(message);
        conversationService.publishMessageUpdateEvent(saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse revokeMessage(String userId, String messageId) {
        Message message = getAccessibleMessage(messageId, userId);
        if (message.getType() == MessageType.SYSTEM) {
            throw new IllegalArgumentException("Khong the thu hoi system message");
        }
        if (message.getUser() == null || !userId.equals(message.getUser().getUserId())) {
            throw new IllegalArgumentException("Chi nguoi gui moi co the thu hoi tin nhan");
        }
        if (message.getDeletedTimeStamp() != null) {
            return conversationService.toMessageResponse(message, userId);
        }

        message.getSnapshots().add(buildSnapshot(message));
        message.setContent("Tin nhan da duoc thu hoi");
        message.setAttachments(new ArrayList<>());
        message.setStickers(new ArrayList<>());
        message.setMentions(new ArrayList<>());
        message.setMentionEveryone(false);
        message.setReference(null);
        message.setPoll(null);
        message.setCall(null);
        message.setPinned(false);
        message.setReactions(new ArrayList<>());
        message.setDeletedTimeStamp(Instant.now());
        messageReactionRepository.findByMessageId(messageId).forEach(messageReactionRepository::delete);

        Message saved = messageRepository.save(message);
        Conversation conversation = conversationService.getConversationEntity(saved.getConversationId());
        conversationService.publishMessageEvent(conversation, saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse forwardMessage(String userId, String sourceMessageId, ForwardMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Du lieu gui len khong hop le");
        }

        Message source = getAccessibleMessage(sourceMessageId, userId);
        if (source.getType() == MessageType.SYSTEM) {
            throw new IllegalArgumentException("Khong the chuyen tiep system message");
        }
        if (source.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the chuyen tiep tin nhan da thu hoi");
        }

        boolean hasConversationId = request.getConversationId() != null && !request.getConversationId().isBlank();
        boolean hasTargetUserId = request.getTargetUserId() != null && !request.getTargetUserId().isBlank();
        if (hasConversationId == hasTargetUserId) {
            throw new IllegalArgumentException("Chi duoc chon mot dich forward");
        }

        Conversation targetConversation;
        if (hasConversationId) {
            targetConversation = conversationService.getConversationEntity(request.getConversationId());
            conversationService.getActiveMembership(targetConversation.getId(), userId);
        } else {
            if (userId.equals(request.getTargetUserId())) {
                throw new IllegalArgumentException("Khong the tu nhan tin cho chinh minh");
            }
            userDirectoryService.getRequired(request.getTargetUserId());
            targetConversation = conversationService.ensureDirectConversation(userId, request.getTargetUserId());
            conversationService.getActiveMembership(targetConversation.getId(), userId);
        }

        Message saved = saveMessage(
                targetConversation,
                userId,
                source.getContent(),
                cloneAttachments(source.getAttachments()),
                cloneStickers(source.getStickers()),
                List.of(),
                false,
                null,
                source.getPoll(),
                source.getCall()
        );
        return conversationService.toMessageResponse(saved, userId);
    }

    public DeleteMessageResponse deleteMessageForMe(String userId, String messageId) {
        Message message = getAccessibleMessage(messageId, userId);
        if (!messageHiddenRepository.existsByUserIdAndMessageId(userId, messageId)) {
            MessageHidden hidden = new MessageHidden();
            hidden.setUserId(userId);
            hidden.setConversationId(message.getConversationId());
            hidden.setMessageId(messageId);
            hidden.setMessagePosition(message.getPosition());
            hidden.setHiddenAt(Instant.now());
            messageHiddenRepository.save(hidden);
        }
        conversationService.pushInboxUpdateForUser(message.getConversationId(), userId);
        return DeleteMessageResponse.builder()
                .messageId(messageId)
                .conversationId(message.getConversationId())
                .deletedForMe(true)
                .deletedForEveryone(false)
                .build();
    }

    public MessageResponse deleteMessageForEveryone(String userId, String messageId) {
        Message message = getAccessibleMessage(messageId, userId);
        if (message.getType() == MessageType.SYSTEM) {
            throw new IllegalArgumentException("Khong the xoa system message cho toan bo cuoc tro chuyen");
        }
        if (message.getPoll() != null) {
            throw new IllegalArgumentException("Vui long dung flow xoa poll cho message nay");
        }
        if (message.getUser() == null || !userId.equals(message.getUser().getUserId())) {
            throw new IllegalArgumentException("Chi nguoi gui moi co the xoa tin nhan cho moi nguoi");
        }
        if (Boolean.TRUE.equals(message.getDeletedForEveryone())) {
            return conversationService.toMessageResponse(message, userId);
        }

        message.getSnapshots().add(buildSnapshot(message));
        message.setContent("Tin nhan da bi xoa");
        message.setAttachments(new ArrayList<>());
        message.setStickers(new ArrayList<>());
        message.setMentions(new ArrayList<>());
        message.setMentionEveryone(false);
        message.setReference(null);
        message.setPoll(null);
        message.setCall(null);
        message.setPinned(false);
        message.setReactions(new ArrayList<>());
        message.setDeletedForEveryone(true);
        message.setDeletedTimeStamp(Instant.now());
        messageReactionRepository.findByMessageId(messageId).forEach(messageReactionRepository::delete);

        Message saved = messageRepository.save(message);
        Conversation conversation = conversationService.getConversationEntity(saved.getConversationId());
        conversationService.publishMessageEvent(conversation, saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    public MessageResponse editMessage(String userId, String messageId, EditMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Du lieu gui len khong hop le");
        }

        Message message = getAccessibleMessage(messageId, userId);
        if (message.getType() == MessageType.SYSTEM) {
            throw new IllegalArgumentException("Khong the sua system message");
        }
        if (message.getDeletedTimeStamp() != null) {
            throw new IllegalArgumentException("Khong the sua tin nhan da thu hoi");
        }
        if (message.getUser() == null || !userId.equals(message.getUser().getUserId())) {
            throw new IllegalArgumentException("Chi nguoi gui moi co the sua tin nhan");
        }
        if (message.getContent() == null || message.getContent().isBlank()) {
            throw new IllegalArgumentException("Chi ho tro sua tin nhan van ban");
        }

        String editedContent = request.getContent() == null ? "" : request.getContent().trim();
        if (editedContent.isBlank()) {
            throw new IllegalArgumentException("Tin nhan sau khi sua phai co it nhat mot noi dung de hien thi");
        }

        message.getSnapshots().add(buildSnapshot(message));
        message.setContent(editedContent);
        message.setEditedTimeStamp(Instant.now());
        Message saved = messageRepository.save(message);
        Conversation conversation = conversationService.getConversationEntity(saved.getConversationId());
        conversationService.publishMessageEvent(conversation, saved);
        return conversationService.toMessageResponse(saved, userId);
    }

    private Message saveMessage(
            Conversation conversation,
            String senderId,
            String rawContent,
            List<Attachment> rawAttachments,
            List<Sticker> rawStickers,
            List<User> rawMentions,
            Boolean mentionEveryone,
            MessageReference reference,
            Poll poll,
            CallInfo call
    ) {
        String content = rawContent == null ? "" : rawContent.trim();
        List<Attachment> attachments = rawAttachments == null ? new ArrayList<>() : rawAttachments.stream().filter(item -> item != null && item.getUrl() != null && !item.getUrl().isBlank()).toList();
        List<Sticker> stickers = rawStickers == null ? new ArrayList<>() : rawStickers.stream().filter(item -> item != null && item.getUrl() != null && !item.getUrl().isBlank()).toList();
        List<User> mentions = rawMentions == null ? new ArrayList<>() : rawMentions;

        boolean hasContent = !content.isBlank();
        boolean hasAttachments = !attachments.isEmpty();
        boolean hasStickers = !stickers.isEmpty();
        boolean hasReference = reference != null;
        boolean hasPoll = poll != null;
        boolean hasCall = call != null;

        if (!hasContent && !hasAttachments && !hasStickers && !hasReference && !hasPoll && !hasCall) {
            throw new IllegalArgumentException("Tin nhan phai co it nhat mot noi dung de hien thi");
        }

        UserProfileDocument profile = userDirectoryService.getRequired(senderId);
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setUser(new User(senderId, profile.getDisplayName(), profile.getAvatarUrl()));
        message.setType(resolveStoredMessageType());
        message.setPosition(conversationService.nextPosition(conversation.getId()));
        message.setContent(content);
        message.setAttachments(new ArrayList<>(attachments));
        message.setStickers(new ArrayList<>(stickers));
        message.setMentions(new ArrayList<>(mentions));
        message.setMentionEveryone(Boolean.TRUE.equals(mentionEveryone));
        message.setReference(reference);
        message.setPoll(poll);
        message.setCall(call);
        message.setTimeStamp(Instant.now());
        Message saved = messageRepository.save(message);
        conversationService.updateConversationAfterMessage(conversation, saved);
        conversationService.publishMessageEvent(conversation, saved);
        return saved;
    }

    private List<User> normalizeMentions(String conversationId, List<User> rawMentions) {
        if (rawMentions == null || rawMentions.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> mentions = new ArrayList<>();
        for (User item : rawMentions) {
            if (item == null || item.getUserId() == null || item.getUserId().isBlank()) {
                continue;
            }
            conversationService.getActiveMembership(conversationId, item.getUserId());
            UserProfileDocument profile = userDirectoryService.getRequired(item.getUserId());
            mentions.add(new User(profile.getId(), profile.getDisplayName(), profile.getAvatarUrl()));
        }
        return mentions;
    }

    private MessageReference normalizeReference(String conversationId, MessageReference rawReference) {
        if (rawReference == null) {
            return null;
        }

        MessageReference reference = new MessageReference();
        reference.setMessageId(rawReference.getMessageId());
        reference.setConversationId(conversationId);

        if (rawReference.getMessageId() == null || rawReference.getMessageId().isBlank()) {
            reference.setExist(false);
            return reference;
        }

        boolean exists = messageRepository.findById(rawReference.getMessageId())
                .filter(message -> conversationId.equals(message.getConversationId()))
                .isPresent();
        reference.setExist(exists);
        return reference;
    }

    private MessageType resolveStoredMessageType() {
        return MessageType.DEFAULT;
    }

    private Message getAccessibleMessage(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay message"));
        conversationService.getActiveMembership(message.getConversationId(), userId);
        return message;
    }

    private Message refreshMessageState(Message message) {
        message.setReactions(conversationService.toMessageResponse(message, null).getReactions());
        return messageRepository.save(message);
    }

    private MessageSnapshot buildSnapshot(Message message) {
        MessageSnapshot snapshot = new MessageSnapshot();
        snapshot.setType(message.getType());
        snapshot.setContent(message.getContent());
        snapshot.setAttachments(message.getAttachments() == null ? new ArrayList<>() : new ArrayList<>(message.getAttachments()));
        snapshot.setTimeStamp(message.getTimeStamp());
        snapshot.setEditedTimeStamp(message.getEditedTimeStamp());
        snapshot.setFlags(message.getFlags());
        snapshot.setMentions(message.getMentions() == null ? new ArrayList<>() : new ArrayList<>(message.getMentions()));
        snapshot.setStickers(message.getStickers() == null ? new ArrayList<>() : new ArrayList<>(message.getStickers()));
        return snapshot;
    }

    private List<Attachment> cloneAttachments(List<Attachment> attachments) {
        return attachments == null ? new ArrayList<>() : attachments.stream()
                .map(item -> item == null ? null : new Attachment(
                        item.getId(),
                        item.getFileName(),
                        item.getTitle(),
                        item.getDescription(),
                        item.getContentType(),
                        item.getSize(),
                        item.getUrl(),
                        item.getHeight(),
                        item.getWidth(),
                        item.getPlaceHolder(),
                        item.getPlaceHolderVersion(),
                        item.getDurationSecs()
                ))
                .filter(item -> item != null)
                .toList();
    }

    private List<Sticker> cloneStickers(List<Sticker> stickers) {
        return stickers == null ? new ArrayList<>() : stickers.stream()
                .map(item -> item == null ? null : new Sticker(item.getId(), item.getUrl()))
                .filter(item -> item != null)
                .toList();
    }

}

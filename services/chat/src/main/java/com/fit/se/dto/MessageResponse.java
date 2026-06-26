package com.fit.se.dto;

import com.fit.se.entity.ContactCard;
import com.fit.se.entity.LocationPayload;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class MessageResponse {
    private String id;
    private String conversationId;
    private String senderId;
    private String receiverId;
    private String senderName;
    private String senderAvatarUrl;
    private String receiverName;
    private String type;
    private String content;
    private AttachmentResponse attachment;
    private boolean revoked;
    private boolean system;
    private String forwardedFromMessageId;
    private boolean edited;
    private Instant editedAt;
    private Long durationSeconds;
    private LocationPayload location;
    private ContactCard contactCard;
    private PollResponse poll;
    private List<ReadReceiptResponse> readReceipts;
    private List<MessageReactionResponse> reactions;
    private Instant createdAt;
    private Instant updatedAt;
    private String displayPosition;
}

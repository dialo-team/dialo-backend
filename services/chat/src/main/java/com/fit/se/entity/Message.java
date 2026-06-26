package com.fit.se.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    @Indexed
    private String conversationId;
    private String senderId;
    private String receiverId;
    private String type;
    private String content;
    private AttachmentMetadata attachment;
    @Builder.Default
    private boolean revoked = false;
    @Builder.Default
    private boolean system = false;
    @Builder.Default
    private Set<String> deletedFor = new HashSet<>();
    @Builder.Default
    private Map<String, Instant> readBy = new HashMap<>();
    @Builder.Default
    private List<MessageReaction> reactions = new ArrayList<>();
    private String forwardedFromMessageId;
    @Builder.Default
    private boolean edited = false;
    private Instant editedAt;
    private Long durationSeconds;
    private LocationPayload location;
    private ContactCard contactCard;
    private PollPayload poll;
    @Indexed
    private Instant createdAt;
    private Instant updatedAt;
}

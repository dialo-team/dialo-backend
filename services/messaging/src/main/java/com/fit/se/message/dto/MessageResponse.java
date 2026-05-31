package com.fit.se.message.dto;

import com.fit.se.user.domain.User;
import com.fit.se.attachment.domain.Attachment;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.message.domain.MessageReference;
import com.fit.se.poll.domain.Poll;
import com.fit.se.message.domain.Reaction;
import com.fit.se.message.domain.Sticker;
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
    private String senderName;
    private String senderAvatarUrl;
    private String type;
    private String content;
    private boolean system;
    private Long position;
    private List<Attachment> attachments;
    private List<Sticker> stickers;
    private List<User> mentions;
    private Boolean mentionEveryone;
    private MessageReference reference;
    private Poll poll;
    private CallInfo call;
    private List<Reaction> reactions;
    private List<MessageReadByResponse> readBy;
    private Boolean pinned;
    private Instant createdAt;
    private Instant editedTimeStamp;
    private Instant deletedTimeStamp;
    private Boolean deleted;
    private Boolean deletedForEveryone;
    private String displayPosition;
}

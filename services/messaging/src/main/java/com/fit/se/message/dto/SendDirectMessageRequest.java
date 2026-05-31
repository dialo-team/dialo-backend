package com.fit.se.message.dto;

import com.fit.se.user.domain.User;
import com.fit.se.attachment.domain.Attachment;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.message.domain.MessageReference;
import com.fit.se.poll.domain.Poll;
import com.fit.se.message.domain.Sticker;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SendDirectMessageRequest {
    @NotBlank
    private String targetUserId;
    private String type;
    private String content;
    private List<Attachment> attachments = new ArrayList<>();
    private List<Sticker> stickers = new ArrayList<>();
    private List<User> mentions = new ArrayList<>();
    private Boolean mentionEveryone = false;
    private MessageReference reference;
    private Poll poll;
    private CallInfo call;
}
package com.fit.se.message.domain;

import com.fit.se.attachment.domain.Attachment;
import com.fit.se.user.domain.User;
import com.fit.se.message.domain.Sticker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageSnapshot {
    private MessageType type;
    private String content;
    private List<Attachment> attachments = new ArrayList<>();
    private Instant timeStamp;
    private Instant editedTimeStamp;
    private Integer flags = 0;
    private List<User> mentions = new ArrayList<>();
    private List<Sticker> stickers = new ArrayList<>();
}

package com.fit.se.message.domain;

import com.fit.se.attachment.domain.Attachment;
import com.fit.se.call.domain.CallInfo;
import com.fit.se.poll.domain.Poll;
import com.fit.se.user.domain.User;
import com.fit.se.message.domain.Sticker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "messages")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_conversation_position",
                def = "{'conversationId': 1, 'position': -1}"
        ),
        @CompoundIndex(
                name = "idx_conversation_created_at",
                def = "{'conversationId': 1, 'createdAt': -1}"
        )
})
public class Message {

    @Id
    private String id;

    @Indexed
    private String conversationId;

    private User user;

    private MessageType type;

    private Long position;

    private String content;

    private List<Attachment> attachments = new ArrayList<>();
    private List<Sticker> stickers = new ArrayList<>();
    private List<User> mentions = new ArrayList<>();
    private List<MessageSnapshot> snapshots = new ArrayList<>();
    private List<Reaction> reactions = new ArrayList<>();

    private Boolean mentionEveryone = false;

    private MessageReference reference;

    private Poll poll;
    private CallInfo call;

    private Integer flags = 0;
    private Boolean pinned = false;
    private Boolean deletedForEveryone = false;

    private Instant timeStamp;
    private Instant editedTimeStamp;
    private Instant deletedTimeStamp;
}

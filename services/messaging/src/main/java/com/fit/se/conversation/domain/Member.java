package com.fit.se.conversation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "conversation_members")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_conversation_user",
                def = "{'conversationId': 1, 'userId': 1}",
                unique = true
        ),
        @CompoundIndex(
                name = "idx_user_conversation",
                def = "{'userId': 1, 'conversationId': 1}"
        )
})
public class Member {

    @Id
    private String id;

    private String conversationId;
    private String userId;

    private String nick;
    private String avatarUrl;

    private MemberRole role;

    private Instant joinedAt;
    private Instant leftAt;

    private Long lastReadPosition;
    private String lastReadMessageId;
    private Instant lastReadAt;

    private Boolean muted;
    private Boolean pinned;
    private Boolean archived;
}
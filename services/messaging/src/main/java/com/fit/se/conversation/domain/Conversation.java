package com.fit.se.conversation.domain;

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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "conversations")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_type_direct_key",
                def = "{'type': 1, 'directKey': 1}",
                unique = true,
                sparse = true
        ),
        @CompoundIndex(
                name = "idx_last_message_at",
                def = "{'lastMessageAt': -1}"
        )
})
public class Conversation {

    @Id
    private String id;

    @Indexed
    private ConversationType type;

    // Chỉ dùng cho GROUP
    private String name;
    private String avatarUrl;
    private String ownerId;
    private String description;

    // Chỉ dùng cho DIRECT để chống tạo trùng 1-1 conversation
    private String directKey;

    private String lastMessageId;
    private Instant lastMessageAt;

    private Instant createdAt;
    private Instant updatedAt;

    private ConversationStatus status;
}
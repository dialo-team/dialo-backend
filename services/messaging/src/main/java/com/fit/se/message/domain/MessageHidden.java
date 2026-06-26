package com.fit.se.message.domain;

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
@Document(collection = "message_hidden")
@CompoundIndexes({
        @CompoundIndex(
                name = "uk_user_message_hidden",
                def = "{'userId': 1, 'messageId': 1}",
                unique = true
        ),
        @CompoundIndex(
                name = "idx_user_conversation_hidden",
                def = "{'userId': 1, 'conversationId': 1, 'hiddenAt': -1}"
        )
})
public class MessageHidden {

    @Id
    private String id;

    private String userId;
    private String conversationId;
    private String messageId;

    private Long messagePosition;

    private Instant hiddenAt;
}
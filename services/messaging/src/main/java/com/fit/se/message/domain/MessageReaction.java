package com.fit.se.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "message_reactions")
@CompoundIndexes({
        @CompoundIndex(
                name = "uk_message_user_emoji",
                def = "{'messageId': 1, 'userId': 1, 'emoji': 1}",
                unique = true
        ),
        @CompoundIndex(
                name = "idx_message_emoji",
                def = "{'messageId': 1, 'emoji': 1}"
        ),
        @CompoundIndex(
                name = "idx_conversation_message",
                def = "{'conversationId': 1, 'messageId': 1}"
        )
})
public class MessageReaction {
    @Id
    private String id;

    private String messageId;
    private String conversationId;
    private String userId;

    private int emoji;
    private int quantity;
}

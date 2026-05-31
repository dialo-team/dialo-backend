package com.fit.se.poll.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "poll_votes")
@CompoundIndexes({
        @CompoundIndex(
                name = "uk_poll_user_answer",
                def = "{'messageId': 1, 'userId': 1, 'answerId': 1}",
                unique = true
        ),
        @CompoundIndex(
                name = "idx_poll_answer",
                def = "{'messageId': 1, 'answerId': 1}"
        ),
        @CompoundIndex(
                name = "idx_poll_user",
                def = "{'messageId': 1, 'userId': 1}"
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollVote {

    @Id
    private String id;

    private String messageId;

    private String conversationId;

    private String userId;

    private int answerId;

    private Instant createdAt;
}
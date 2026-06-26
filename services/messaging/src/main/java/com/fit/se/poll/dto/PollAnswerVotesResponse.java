package com.fit.se.poll.dto;

import com.fit.se.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PollAnswerVotesResponse {
    private String conversationId;
    private String messageId;
    private int answerId;
    private int total;
    private List<User> voters;
}

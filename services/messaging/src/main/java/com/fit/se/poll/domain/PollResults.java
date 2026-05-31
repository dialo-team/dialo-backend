package com.fit.se.poll.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PollResults {
    private boolean isFinalized;
    private List<PollAnswerCount> answerCounts;
    private List<Integer> selectedAnswerIds;
}

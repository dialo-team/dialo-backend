package com.fit.se.poll.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Poll {
    private String question;
    private List<PollAnswer> answers;
    private Instant expiry;
    private boolean allowMultiSelect;
    private PollResults results;
}

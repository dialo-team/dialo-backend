package com.fit.se.poll.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VotePollRequest {
    private List<Integer> answerIds = new ArrayList<>();
}

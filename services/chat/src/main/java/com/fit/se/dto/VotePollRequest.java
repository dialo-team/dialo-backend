package com.fit.se.dto;

import lombok.Data;

import java.util.List;

@Data
public class VotePollRequest {
    private List<String> optionIds;
}

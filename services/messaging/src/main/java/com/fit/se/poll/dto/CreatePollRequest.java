package com.fit.se.poll.dto;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePollRequest {
    private String content;
    private String question;
    private List<String> options = new ArrayList<>();
    private Instant expiry;
    private Boolean allowMultiSelect = false;
}

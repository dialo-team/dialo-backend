package com.fit.se.call.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateCallRequest {
    private String type;
    private List<String> userIds = new ArrayList<>();
}

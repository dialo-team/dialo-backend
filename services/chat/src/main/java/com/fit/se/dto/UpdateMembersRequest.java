package com.fit.se.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMembersRequest {

    @NotEmpty(message = "memberIds must not be empty")
    private List<String> memberIds;
}
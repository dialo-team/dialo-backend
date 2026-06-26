package com.fit.se.dto;

import lombok.Data;

@Data
public class FriendAcceptedPayload {
    private String user1Id;
    private String user2Id;
    private String systemMessage;
}

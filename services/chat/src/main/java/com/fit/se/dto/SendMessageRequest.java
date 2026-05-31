package com.fit.se.dto;

import com.fit.se.entity.ContactCard;
import com.fit.se.entity.LocationPayload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank
    private String conversationId;
    @NotBlank
    private String senderId;
    @NotBlank
    private String type;
    private String content;
    private Long durationSeconds;
    private LocationPayload location;
    private ContactCard contactCard;
}

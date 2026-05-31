package com.fit.se.dto;

import com.fit.se.entity.ContactCard;
import com.fit.se.entity.LocationPayload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditMessageRequest {
    @NotBlank
    private String content;
    private Long durationSeconds;
    private LocationPayload location;
    private ContactCard contactCard;
}

package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentResponse {
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long size;
    private String thumbnailUrl;
}

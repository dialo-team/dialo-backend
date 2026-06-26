package com.fit.se.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentMetadata {
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long size;
    private String thumbnailUrl;
}

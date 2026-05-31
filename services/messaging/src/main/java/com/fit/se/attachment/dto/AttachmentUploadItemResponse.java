package com.fit.se.attachment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentUploadItemResponse {
    private String id;
    private String fileName;
    private String contentType;
    private long size;
    private String url;
    private Double width;
    private Double height;
}
package com.fit.se.attachment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AttachmentUploadResponse {
    private List<AttachmentUploadItemResponse> items;
}
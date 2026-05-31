package com.fit.se.attachment.controller;

import com.fit.se.attachment.dto.AttachmentUploadResponse;
import com.fit.se.attachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public AttachmentUploadResponse upload(@RequestParam("files") List<MultipartFile> files) {
        return attachmentService.upload(files);
    }
}
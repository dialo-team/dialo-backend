package com.fit.se.attachment.service;

import com.fit.se.attachment.dto.AttachmentUploadItemResponse;
import com.fit.se.attachment.dto.AttachmentUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final S3Client s3Client;

    @Value("${app.upload.s3.bucket}")
    private String bucketName;

    @Value("${app.upload.s3.key-prefix:messaging/attachments}")
    private String keyPrefix;

    @Value("${app.upload.s3.public-base-url:}")
    private String publicBaseUrl;

    @Value("${spring.cloud.aws.region.static}")
    private String awsRegion;

    public AttachmentUploadResponse upload(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Danh sach file khong duoc de trong");
        }
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("Chua cau hinh app.upload.s3.bucket");
        }

        List<AttachmentUploadItemResponse> items = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            items.add(storeFile(file));
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Khong co file hop le de upload");
        }
        return AttachmentUploadResponse.builder().items(items).build();
    }

    private AttachmentUploadItemResponse storeFile(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename() == null || file.getOriginalFilename().isBlank() ? "file" : Paths.get(file.getOriginalFilename()).getFileName().toString();
            String extension = extractExtension(originalName);
            String id = UUID.randomUUID().toString();
            String storedName = extension.isBlank() ? id : id + "." + extension;
            String objectKey = buildObjectKey(storedName);

            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(file.getContentType())
                        .contentLength(file.getSize())
                        .build();
                s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            }

            Double width = null;
            Double height = null;
            if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                try (InputStream imageStream = file.getInputStream()) {
                    BufferedImage image = ImageIO.read(imageStream);
                    if (image != null) {
                        width = (double) image.getWidth();
                        height = (double) image.getHeight();
                    }
                }
            }

            return AttachmentUploadItemResponse.builder()
                    .id(id)
                    .fileName(originalName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .url(resolvePublicUrl(objectKey))
                    .width(width)
                    .height(height)
                    .build();
        } catch (IOException | S3Exception ex) {
            throw new IllegalStateException("Upload file that bai", ex);
        }
    }

    private String buildObjectKey(String storedName) {
        String normalizedPrefix = keyPrefix == null ? "" : keyPrefix.trim();
        if (normalizedPrefix.isEmpty()) {
            return storedName;
        }
        String prefixWithoutSlashes = normalizedPrefix.replaceAll("^/+", "").replaceAll("/+$", "");
        return prefixWithoutSlashes + "/" + storedName;
    }

    private String resolvePublicUrl(String objectKey) {
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
            return base + "/" + objectKey;
        }
        return "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + objectKey;
    }

    private String extractExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1);
    }
}

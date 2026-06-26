package com.fit.se.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        try {
            Files.createDirectories(Paths.get(uploadDir));
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
            String savedName = UUID.randomUUID() + "_" + originalFileName;
            Path targetPath = Paths.get(uploadDir).resolve(savedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String mimeType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
            return StoredFile.builder()
                    .originalFileName(originalFileName)
                    .savedFileName(savedName)
                    .mimeType(mimeType)
                    .size(file.getSize())
                    .publicUrl("/uploads/" + savedName)
                    .absolutePath(targetPath)
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("Could not store file", e);
        }
    }

    @Getter
    @Builder
    public static class StoredFile {
        private final String originalFileName;
        private final String savedFileName;
        private final String mimeType;
        private final long size;
        private final String publicUrl;
        private final Path absolutePath;
    }
}

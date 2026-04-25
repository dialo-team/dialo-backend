package com.fit.se.application.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadPublicFile(MultipartFile file, String folder);
}
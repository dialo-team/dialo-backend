package com.fit.se.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadPublicFile(MultipartFile file, String folder);
}
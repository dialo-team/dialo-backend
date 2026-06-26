package com.fit.se.user.application.command.background;

import com.fit.se.common.storage.FileStorageService;
import com.fit.se.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateBackgroundCommandHandler {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public void execute(UpdateBackgroundCommand cmd, MultipartFile file) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String backgroundUrl = fileStorageService.uploadPublicFile(file, "users/background");

        user.updateBackground(backgroundUrl);
        userRepository.save(user);
    }
}
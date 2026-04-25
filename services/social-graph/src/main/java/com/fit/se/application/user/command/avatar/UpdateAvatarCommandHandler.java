package com.fit.se.application.user.command.avatar;

import com.fit.se.application.common.storage.FileStorageService;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateAvatarCommandHandler {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public void execute(UpdateAvatarCommand cmd, MultipartFile file) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String avatarUrl = fileStorageService.uploadPublicFile(file, "users/avatar");

        user.updateAvatar(avatarUrl);
        userRepository.save(user);
    }
}
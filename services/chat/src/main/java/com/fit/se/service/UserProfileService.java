package com.fit.se.service;

import com.fit.se.config.UserCreatedEvent;
import com.fit.se.dto.UserProfileRequest;
import com.fit.se.dto.UserProfileResponse;
import com.fit.se.entity.UserProfile;
import com.fit.se.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse upsert(UserProfileRequest request) {
        Instant now = Instant.now();
        UserProfile profile = userProfileRepository.findById(request.getId())
                .map(existing -> {
                    existing.setDisplayName(request.getDisplayName());
                    existing.setAvatarUrl(request.getAvatarUrl());
                    existing.setUpdatedAt(now);
                    return existing;
                })
                .orElse(UserProfile.builder()
                        .id(request.getId())
                        .displayName(request.getDisplayName())
                        .avatarUrl(request.getAvatarUrl())
                        .createdAt(now)
                        .updatedAt(now)
                        .build());
        return toResponse(userProfileRepository.save(profile));
    }

    public UserProfileResponse getById(String userId) {
        return toResponse(findEntity(userId));
    }

    public UserProfile findEntity(String userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    public UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .build();
    }

    @RabbitListener(queues = "user.created")
    public void handleUserCreated(UserCreatedEvent event) {
        if (event == null || event.userId() == null || event.userId().isBlank()) {
            return;
        }

        Instant now = Instant.now();

        userProfileRepository.findById(event.userId())
                .map(existing -> {
                    if (existing.getDisplayName() == null || existing.getDisplayName().isBlank()) {
                        existing.setDisplayName(event.phone() == null ? "" : event.phone());
                    }
                    if (existing.getAvatarUrl() == null) {
                        existing.setAvatarUrl("");
                    }
                    existing.setUpdatedAt(now);
                    return userProfileRepository.save(existing);
                })
                .orElseGet(() -> userProfileRepository.save(
                        UserProfile.builder()
                                .id(event.userId())
                                .displayName(event.phone() == null ? "" : event.phone())
                                .avatarUrl("")
                                .createdAt(now)
                                .updatedAt(now)
                                .build()
                ));
    }
}
